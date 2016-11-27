package com.elipcero.SpringOauth2MobileClient;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class CustomTokenEnhancer implements TokenEnhancer {
	
	@NonNull private final ProfileConfigurationService profileService;
	
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    	        
        final Map<String, Object> additionalInfo = new HashMap<>();
        
        additionalInfo.put("profile", this.profileService.configure(accessToken, authentication).getId());

        ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }
}
