package com.demo.product.review.security.rest;

public class TokenUser {
	private Long id;
	private String userName;
	private long expires;
	private String authority;
	private String userType;
	public TokenUser(){
		
	}
	public TokenUser(Long id, String userName, long expires, String authority, String userType){
		this.id = id;
		this.userName =userName;
		this.expires = expires;
		this.authority = authority;
		this.userType=userType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public long getExpires() {
		return expires;
	}
	public void setExpires(long expires) {
		this.expires = expires;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
}
