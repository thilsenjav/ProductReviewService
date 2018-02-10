package com.demo.product.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.demo.product.review.security.rest.RestAuthenticationAccessDeniedHandler;
import com.demo.product.review.security.rest.RestAuthenticationEntryPoint;
import com.demo.product.review.security.rest.RestAuthenticationFailureHandler;
import com.demo.product.review.security.rest.RestAuthenticationSuccessHandler;
import com.demo.product.review.security.rest.StatelessAuthenticationFilter;
import com.demo.product.review.security.rest.TokenAuthenticationService;
import com.demo.product.review.service.UserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	public WebSecurityConfig() {
		super(true);
	}

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Autowired
	RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;

	@Autowired
	RestAuthenticationFailureHandler restAuthenticationFailureHandler;

	@Autowired
	RestAuthenticationAccessDeniedHandler restAuthenticationAccessDeniedHandler;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
				.accessDeniedHandler(restAuthenticationAccessDeniedHandler).and().formLogin()
				.successHandler(restAuthenticationSuccessHandler).failureHandler(restAuthenticationFailureHandler).and()
				.anonymous().and().servletApi().and().authorizeRequests().antMatchers("/**/*.jpg").permitAll()
				.antMatchers("/images/**").permitAll().antMatchers(HttpMethod.POST,"/api/review/login").permitAll()
				.antMatchers("/api/review/add").permitAll().anyRequest().authenticated().and()
				/*.addFilterBefore(new LoginFilter("/api/user/login", userDetailsService, authenticationManager(),tokenAuthenticationService),
						UsernamePasswordAuthenticationFilter.class)*/
				.addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class);

		http.csrf().disable();
		// http.formLogin()

	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}
}
