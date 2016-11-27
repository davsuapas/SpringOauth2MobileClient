package com.elipcero.SpringOauth2MobileClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MongoDBTokenStore implements TokenStore {
	
	private static final Log LOG = LogFactory.getLog(TokenStore.class);
	
    private final OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

    private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    private final AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public MongoDBTokenStore(OAuth2AccessTokenRepository oAuth2AccessTokenRepository,
                             OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository) {
    	
        this.oAuth2AccessTokenRepository = oAuth2AccessTokenRepository;
        this.oAuth2RefreshTokenRepository = oAuth2RefreshTokenRepository;
    }
    
    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
    	
    	OAuth2AccessToken authenticationAccessToken = null;

    	String key = authenticationKeyGenerator.extractKey(authentication);
    	
    	try {
    		authenticationAccessToken = oAuth2AccessTokenRepository.findByAuthenticationId(key).getoAuth2AccessToken();
    	}
        catch (Exception e) {
        	LOG.error("Could not extract access token for authentication " + authentication, e);
		}
        
        if (authenticationAccessToken == null) {
        	if (LOG.isInfoEnabled()) {
        		LOG.debug("Failed to find access token for authentication " + authentication);
        	}
        }    	
    	
        if (authenticationAccessToken != null && 
        		!key.equals(authenticationKeyGenerator.extractKey(
        				readAuthentication(authenticationAccessToken.getValue())))) {
        	
			removeAccessToken(authenticationAccessToken.getValue());
			storeAccessToken(authenticationAccessToken, authentication);
        }
        
        return authenticationAccessToken;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }
    
    @Override
    public OAuth2AccessToken readAccessToken(String tokenId) {

    	OAuth2AccessToken authenticationAccessToken = null;
    	
    	try {
    		authenticationAccessToken = oAuth2AccessTokenRepository.findByTokenId(extractTokenKey(tokenId)).getoAuth2AccessToken();
    	}
        catch (Exception e) {
			LOG.warn("Failed to deserialize access token for " + tokenId, e);
        	removeAccessToken(tokenId);
		}
        
        if (authenticationAccessToken == null) {
        	if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token for token " + tokenId);
        	}
        }
        
        return authenticationAccessToken;
    }
    
    @Override
    public OAuth2Authentication readAuthentication(String tokenId) {
    	
    	OAuth2Authentication authentication = null;
    	
    	try {
    		authentication = oAuth2AccessTokenRepository.findByTokenId(extractTokenKey(tokenId)).getAuthentication();
    	}
        catch (Exception e) {
			LOG.warn("Failed to deserialize authentication for " + tokenId, e);
        	removeAccessToken(tokenId);
		}
        
        if (authentication == null) {
        	if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token for token " + tokenId);
        	}
        }
        
        return authentication;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
    	
    	String refreshTokenId = null;
    	
        OAuth2RefreshToken refreshToken = token.getRefreshToken();
        
        if (refreshToken != null) {
        	refreshTokenId = extractTokenKey(refreshToken.getValue());
        }    	
    	
    	if (readAccessToken(token.getValue()) != null) {
			removeAccessToken(token.getValue());
    	}		
    	
        OAuth2AuthenticationAccessToken oAuth2AuthenticationAccessToken =
        			new OAuth2AuthenticationAccessToken(
        					extractTokenKey(token.getValue()),
        					refreshTokenId,
        					token,
        					authentication,
        					authenticationKeyGenerator.extractKey(authentication)
        			);
        
        oAuth2AccessTokenRepository.save(oAuth2AuthenticationAccessToken);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
    	this.removeAccessToken(token.getValue());
    }
    
    private void removeAccessToken(String tokenId) {
    	
        OAuth2AuthenticationAccessToken accessToken = oAuth2AccessTokenRepository.findByTokenId(extractTokenKey(tokenId));
        
        if(accessToken != null) {
            oAuth2AccessTokenRepository.delete(accessToken);
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        oAuth2RefreshTokenRepository.save(new OAuth2AuthenticationRefreshToken(extractTokenKey(refreshToken.getValue()), refreshToken, authentication));
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenId) {
    	
    	OAuth2RefreshToken refreshToken = null;
    	
    	try {
    		refreshToken = oAuth2RefreshTokenRepository.findByTokenId(extractTokenKey(tokenId)).getoAuth2RefreshToken();
    	}
        catch (Exception e) {
        	LOG.warn("Failed to deserialize refresh token for token " + tokenId, e);
        	removeRefreshToken(tokenId);
		}
        
        if (refreshToken == null) {
        	if (LOG.isInfoEnabled()) {
        		LOG.info("Failed to find refresh token for token " + tokenId);
        	}
        }
        
        return refreshToken;    	
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
    	
    	OAuth2Authentication authentication = null;
    	
    	try {
    		authentication = oAuth2RefreshTokenRepository.findByTokenId(token.getValue()).getAuthentication();
    	}
        catch (Exception e) {
        	LOG.warn("Failed to deserialize access token for " + token.getValue(), e);
        	removeRefreshToken(token.getValue());
		}
        
        if (authentication == null) {
        	if (LOG.isInfoEnabled()) {
        		LOG.info("Failed to find access token for token " + token.getValue());
        	}
        }
        
        return authentication;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
    	removeRefreshToken(token.getValue());
    }
    
    private void removeRefreshToken(String tokenId) {
    	oAuth2RefreshTokenRepository.delete(oAuth2RefreshTokenRepository.findByTokenId(extractTokenKey(tokenId)));
    }    

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        oAuth2AccessTokenRepository.delete(oAuth2AccessTokenRepository.findByRefreshTokenId(extractTokenKey(refreshToken.getValue())));
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
    	
        List<OAuth2AuthenticationAccessToken> tokens = oAuth2AccessTokenRepository.findByClientId(clientId);
        
        if (tokens.size() == 0) {
        	if (LOG.isInfoEnabled()) {
        		LOG.info("Failed to find access token for clientId " + clientId);
        	}
        }
        
        return extractAccessTokens(tokens);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
    	
        List<OAuth2AuthenticationAccessToken> tokens = oAuth2AccessTokenRepository.findByClientIdAndUserName(clientId, userName);
        
        if (tokens.size() == 0) {
        	if (LOG.isInfoEnabled()) {
        		LOG.info("Failed to find access token for clientId " + clientId + " and userName " + userName);
        	}
        }
        
        return extractAccessTokens(tokens);
    }

    private Collection<OAuth2AccessToken> extractAccessTokens(List<OAuth2AuthenticationAccessToken> tokens) {
    	
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
        
        for(OAuth2AuthenticationAccessToken token : tokens) {
            accessTokens.add(token.getoAuth2AccessToken());
        }
        
        return accessTokens;
    }
    
    private static String extractTokenKey(String value) {
    	
		if (value == null) {
			return null;
		}
		
		MessageDigest digest;
		
		try {
			digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}
}