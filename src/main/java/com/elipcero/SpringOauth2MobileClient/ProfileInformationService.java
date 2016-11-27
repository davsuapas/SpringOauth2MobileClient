package com.elipcero.SpringOauth2MobileClient;

import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@Service
public class ProfileInformationService {

	@NonNull private final AuthorizationServerTokenServices tokenServices;
	
	@NonNull private final ProfileRepository profileRepository;
	
	public Profile GetProfile(OAuth2Authentication authentication) {
		
		Map<String, Object> additionalInfo = tokenServices.getAccessToken(authentication).getAdditionalInformation();

        return this.profileRepository.findOne((String)additionalInfo.get("profile"));
	}
}
