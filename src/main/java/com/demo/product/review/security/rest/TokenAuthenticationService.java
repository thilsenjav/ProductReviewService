package com.demo.product.review.security.rest;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
public class TokenAuthenticationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationService.class);
	private String authHeaderName;
	private long expiresMS;

	private final TokenHandler tokenHandler;

	@Autowired
	public TokenAuthenticationService(@Value("${app.token.secret}") String secret, @Value("${app.token.name}") String tokenName,
			@Value("${app.token.expiresMS}") long expiresMillisec) {
		tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(secret));
		this.authHeaderName = tokenName;
		this.expiresMS = expiresMillisec;
	}
	private String removeLastChar(String str) {
	    if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
	        str = str.substring(0, str.length() - 1);
	    }
	    return str;
	}
	
	public String createTokenForUser(User user) {
		String authority="";
		if (user.getAuthorities()!=null && user.getAuthorities().size()>0) {
			StringBuffer authBuffer = new StringBuffer();
			for (GrantedAuthority ga : user.getAuthorities()) {
				authBuffer.append(ga.getAuthority()).append(",");
			}
			authority=removeLastChar(authBuffer.toString());
		}
		TokenUser tokenUser=new TokenUser(0L,user.getUsername(),System.currentTimeMillis()+expiresMS,authority,"USER");
		tokenUser.setExpires(System.currentTimeMillis() + expiresMS);
		return tokenHandler.createTokenForUser(tokenUser);
	}
//
	public void addAuthentication(HttpServletResponse response, Authentication authentication) {
		final User user = (User) authentication.getPrincipal();
		response.addHeader(authHeaderName, createTokenForUser( user));
	}

	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getHeader(authHeaderName);
		if (token == null) {
			final Cookie cookie = WebUtils.getCookie(request, authHeaderName);
			if (cookie != null) {
				token = cookie.getValue();
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				
			}
		}

		return getUserAuthFromToken(token);
	}
	private Authentication getUserAuthFromToken(String token) {
		if (token != null) {
			final TokenUser tokenUser = tokenHandler.parseUserFromToken(token);
			LOGGER.debug("CurrentUser-------------: " + tokenUser);
			if (tokenUser != null) {
				User user=new User(tokenUser.getUserName(),"",true,true,true,true,AuthorityUtils.createAuthorityList(tokenUser.getAuthority().split(",")));
				final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(
						user, user.getPassword(), user.getAuthorities());
				return loginToken;
			}
		}
		return null;
	}
}