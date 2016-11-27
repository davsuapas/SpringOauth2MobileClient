package com.elipcero.SpringOauth2MobileClient;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OAuth2RefreshTokenRepository extends MongoRepository<OAuth2AuthenticationRefreshToken, String> {

    public OAuth2AuthenticationRefreshToken findByTokenId(String tokenId);
}