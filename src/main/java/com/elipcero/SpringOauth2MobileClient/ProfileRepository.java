package com.elipcero.SpringOauth2MobileClient;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<Profile, String> {
	
	public Optional<Profile> findByProviderAndUserName(String provider, String UserName); 
}
