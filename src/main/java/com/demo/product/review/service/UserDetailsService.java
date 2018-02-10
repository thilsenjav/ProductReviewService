package com.demo.product.review.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.demo.product.review.dto.UserDto;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	
	@Autowired
	private UserClient userClient;
	
	
	public UserDetailsService() {		
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserDto user;
		try {
			user = userClient.findByEmail(email);
		} catch (Exception e) {
			throw new UsernameNotFoundException(e.getMessage()); 
		}
		if(null==user) {
			throw new UsernameNotFoundException("No user found with email : "+email);
		}
		 
		return new org.springframework.security.core.userdetails.User(email,user.getPassword(),user.isEnabled(),true,true,true,getGrantedAuthorities());
	}

	private final Collection<? extends GrantedAuthority> getGrantedAuthorities() {
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("USER"));


		return authorities;
	}
}
