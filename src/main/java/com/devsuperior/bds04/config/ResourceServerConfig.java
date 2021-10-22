package com.devsuperior.bds04.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer		
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

	@Autowired
	private Environment env;
	
	private static final String[] PATH_PUBLIC = {"/oauth/token", "/h2-console/**"};
	private static final String[] PATH_GET = {"/cities/**","/events/**"};
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		// Liberar as abas (frames) do banco H2
		if(Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		http.authorizeRequests()
		.antMatchers(PATH_PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, PATH_GET).permitAll()
		.antMatchers(HttpMethod.POST, "/events/**").hasAnyRole("CLIENT","ADMIN")
		.antMatchers(HttpMethod.POST, "/cities/**").hasAnyRole("ADMIN")
		.anyRequest().hasAnyRole("ADMIN");
	}
}