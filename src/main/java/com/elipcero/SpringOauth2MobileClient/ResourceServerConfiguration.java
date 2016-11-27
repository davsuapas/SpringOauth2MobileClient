package com.elipcero.SpringOauth2MobileClient;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.sessionManagement()
        		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        	.and()
        	.requestMatcher(new OrRequestMatcher(
					new AntPathRequestMatcher("/hello"), 
					new AntPathRequestMatcher("/revokeToken")))
			.authorizeRequests()
			.anyRequest().access("#oauth2.hasScope('trust')");
			
	}
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("mobileclient");
	}

}
