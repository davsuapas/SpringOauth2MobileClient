package com.elipcero.SpringOauth2MobileClient;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.common.util.SerializationUtils;

import java.io.Serializable;

@Document(collection = "oauth2_access_token")
public class OAuth2AuthenticationAccessToken implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    private String id;
	
	@Indexed
    private String tokenId;
	
    @Indexed
    private String refreshTokenId;

    @Indexed
    private String authenticationId;
 	
    @Indexed
    private String clientId;
    
    private String userName;
    private byte[] oAuth2AccessToken;
    private byte[] authentication;
        
    public OAuth2AuthenticationAccessToken() {
    }
    
    public OAuth2AuthenticationAccessToken(String digestTokenId, String digestRefreshTokenId, OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication, String authenticationId) {
       
        this.tokenId = digestTokenId;
        this.oAuth2AccessToken = SerializationUtils.serialize(oAuth2AccessToken);
        this.authenticationId = authenticationId;
        this.userName = authentication.isClientOnly() ? null : authentication.getName();
        this.clientId = authentication.getOAuth2Request().getClientId();
        this.authentication = SerializationUtils.serialize(authentication);
    	this.refreshTokenId = digestRefreshTokenId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public OAuth2AccessToken getoAuth2AccessToken() {
        return SerializationUtils.deserialize(oAuth2AccessToken);
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public String getUserName() {
        return userName;
    }

    public String getClientId() {
        return clientId;
    }

    public OAuth2Authentication getAuthentication() {
        return SerializationUtils.deserialize(authentication);
    }

    public String getRefreshTokenId() {
        return refreshTokenId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAuth2AuthenticationAccessToken that = (OAuth2AuthenticationAccessToken) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
