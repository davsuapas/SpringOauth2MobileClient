package com.elipcero.SpringOauth2MobileClient;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Profile {
	
	public Profile(String provider, String userName) {
		this.provider = provider;
		this.userName = userName;
	}
	
	@Id
	private String id;

	private String provider;
	private String userName;
}
