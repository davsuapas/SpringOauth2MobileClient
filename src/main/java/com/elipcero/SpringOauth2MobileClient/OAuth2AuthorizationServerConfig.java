package com.elipcero.SpringOauth2MobileClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
    @Autowired
    private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

    @Autowired
    private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;
    
    @Autowired
    private ProfileRepository profileRepository;
      
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints)	throws Exception {
		endpoints
			.authenticationManager(authenticationManager)
			.tokenStore(tokenStore())
			.tokenEnhancer(tokenEnhancer());
			
	}	

	@Override
	public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
	 
		clients
			.inMemory()
			.withClient("mobileclient").authorizedGrantTypes("implicit", "client_credentials")
			.autoApprove(true) // El sistema no pregunta mediante el browser para que scope va a funcionar el token
			.scopes("trust").resourceIds("mobileclient");
	} 
	 
	@Bean
	@Primary
	public AuthorizationServerTokenServices tokenServices() {
	    DefaultTokenServices tokenServices = new DefaultTokenServices();
	    
	    tokenServices.setTokenEnhancer(tokenEnhancer());
	    tokenServices.setTokenStore(tokenStore());
	    return tokenServices;
	}

	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer(new ProfileConfigurationService(profileRepository));
	}
	
	@Bean
    public TokenStore tokenStore() {
        return new MongoDBTokenStore(oAuth2AccessTokenRepository, oAuth2RefreshTokenRepository);
    }	
}
