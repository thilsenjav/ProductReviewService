package com.demo.product.review.security.rest;



import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class StatelessAuthenticationFilter extends GenericFilterBean {

	private final TokenAuthenticationService tokenAuthenticationService;
	private static final Logger LOGGER = LoggerFactory.getLogger(StatelessAuthenticationFilter.class);

	public StatelessAuthenticationFilter(TokenAuthenticationService taService) {
		this.tokenAuthenticationService = taService;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			final Authentication authentication = tokenAuthenticationService
					.getAuthentication((HttpServletRequest) req, (HttpServletResponse) res);
			if (authentication != null) {
				SecurityContextHolder.getContext().setAuthentication(authentication);
				LOGGER.debug("Populated SecurityContextHolder with token: '"
						+ SecurityContextHolder.getContext().getAuthentication() + "'");
				// Add the custom token as HTTP header to the response
				tokenAuthenticationService.addAuthentication((HttpServletResponse) res, authentication);
			}

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("SecurityContextHolder not populated with token, as it already contained: '"
						+ SecurityContextHolder.getContext().getAuthentication() + "'");
			}
		}
		chain.doFilter(req, res); // always continue
		/*
		 * Note that unlike most Spring Security related filters, I choose to
		 * continue down the filter chain regardless of successful
		 * authentication. I wanted to support triggering Springâ€™s
		 * AnonymousAuthenticationFilter to support anonymous authentication.
		 * The big difference here being that the filter is not configured to
		 * map to any url specifically meant for authentication, so not
		 * providing the header isnâ€™t really a fault.
		 * 
		 */
	}
}
