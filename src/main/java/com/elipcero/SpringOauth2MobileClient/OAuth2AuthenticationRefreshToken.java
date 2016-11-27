package com.elipcero.SpringOauth2MobileClient;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.common.util.SerializationUtils;

@Document(collection = "oauth2_refresh_token")
public class OAuth2AuthenticationRefreshToken {

    @Id
    private String id;
    
    @Indexed
    private String tokenId;
    
    private byte[] oAuth2RefreshToken;
    private byte[] authentication;
    
    public OAuth2AuthenticationRefreshToken() {
    }

    public OAuth2AuthenticationRefreshToken(String digestRefreshTokenId, OAuth2RefreshToken oAuth2RefreshToken, OAuth2Authentication authentication) {

        this.tokenId = digestRefreshTokenId;
        
        this.oAuth2RefreshToken = SerializationUtils.serialize(oAuth2RefreshToken);
        this.authentication = SerializationUtils.serialize(authentication);
    }

    public String getTokenId() {
        return tokenId;
    }

    public OAuth2RefreshToken getoAuth2RefreshToken() {
        return SerializationUtils.deserialize(oAuth2RefreshToken);
    }

    public OAuth2Authentication getAuthentication() {
        return SerializationUtils.deserialize(authentication);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAuth2AuthenticationRefreshToken that = (OAuth2AuthenticationRefreshToken) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
