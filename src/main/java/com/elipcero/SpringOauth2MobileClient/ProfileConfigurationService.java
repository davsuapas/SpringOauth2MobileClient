package com.elipcero.SpringOauth2MobileClient;

import java.util.Optional;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ProfileConfigurationService {
	
	@NonNull private final ProfileRepository profileRepository;

	public Profile configure(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		String userAuthenticationClientId = ((OAuth2Authentication)authentication.getUserAuthentication()).getOAuth2Request().getClientId();
				
		Optional<Profile> profile =
				this.profileRepository.findByProviderAndUserName(
						userAuthenticationClientId, 
						authentication.getName());
		
		if (!profile.isPresent()) {
			
			return profileRepository.save(
					new Profile(
						userAuthenticationClientId,	
						authentication.getName()
					)
			);
		}
		else {
			return profile.get();
		}
	}
}
