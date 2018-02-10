package com.demo.product.review.security.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.demo.product.review.service.UserDetailsService;

public class WebAuthendicationConfig extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
	}
}
