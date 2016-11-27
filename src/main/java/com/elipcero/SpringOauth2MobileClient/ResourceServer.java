package com.elipcero.SpringOauth2MobileClient;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@RestController
public class ResourceServer {
	
	@NonNull private final ProfileInformationService profileService;
	@NonNull private final ConsumerTokenServices defaultTokenServices;
	
	@RequestMapping("/hello")
	public String getHelloWorld(OAuth2Authentication authentication) {
               
		return String.format("Hello word User: (%s), Profile: (%s)",
				authentication.getUserAuthentication().getName(),
				this.profileService.GetProfile(authentication).toString());
	}
	
	@RequestMapping("/revokeToken")
	public void revokeToken(OAuth2Authentication authentication) {
		
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)authentication.getDetails();
		
		defaultTokenServices.revokeToken(details.getTokenValue());
	}
}
