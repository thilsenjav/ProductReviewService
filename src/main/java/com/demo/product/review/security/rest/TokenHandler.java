package com.demo.product.review.security.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TokenHandler {

	private static final String HMAC_ALGO = "HmacSHA256";
	private static final String SEPARATOR = ".";
	private static final String SEPARATOR_SPLITTER = "\\.";

	private final Mac hmac;

	public TokenHandler(byte[] secretKey) {
		try {
			hmac = Mac.getInstance(HMAC_ALGO);
			hmac.init(new SecretKeySpec(secretKey, HMAC_ALGO));
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IllegalStateException("failed to initialize HMAC: " + e.getMessage(), e);
		}
	}

	public TokenUser parseUserFromToken(String token) {
		final String[] parts = token.split(SEPARATOR_SPLITTER);
		if (parts.length == 2 && parts[0].length() > 0 && parts[1].length() > 0) {
			try {
				final byte[] userBytes = fromBase64(parts[0]);
				final byte[] hash = fromBase64(parts[1]);

				boolean validHash = Arrays.equals(createHmac(userBytes), hash);
				if (validHash) {
					final TokenUser user = fromJSON(userBytes);
					if (new Date().getTime() < user.getExpires()) {
						return user;
					}
				}
			} catch (IllegalArgumentException e) {
				//log tempering attempt here
			}
		}
		return null;
	}

	public String createTokenForUser(TokenUser user) {
		byte[] userBytes = toJSON(user);
		byte[] hash = createHmac(userBytes);
		final StringBuilder sb = new StringBuilder(170);
		sb.append(toBase64(userBytes));
		sb.append(SEPARATOR);
		sb.append(toBase64(hash));
		return sb.toString();
	}

	private TokenUser fromJSON(final byte[] userBytes) {
		try {
			
			TokenUser tokenUser = new ObjectMapper().readValue(new ByteArrayInputStream(userBytes), TokenUser.class);	
			//System.out.println(tokenUser.getUserName());
			return tokenUser;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	

	private byte[] toJSON(TokenUser user) {
		try {
			//String authority="";
			/*if (user.getAuthorities()!=null && user.getAuthorities().size()>0) {
				StringBuffer authBuffer = new StringBuffer();
				for (GrantedAuthority ga : user.getAuthorities()) {
					authBuffer.append(ga.getAuthority()).append(",");
				}
				authority=removeLastChar(authBuffer.toString());
			}*/
			/*TokenUser tokenUser = new TokenUser(user.getId(), user.getUsername(),
					user.getExpires(),authority,user.getUserType());*/
			return new ObjectMapper().writeValueAsBytes(user);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	private String toBase64(byte[] content) {
		return DatatypeConverter.printBase64Binary(content);
	}

	private byte[] fromBase64(String content) {
		return DatatypeConverter.parseBase64Binary(content);
	}

	// synchronized to guard internal hmac object
	private synchronized byte[] createHmac(byte[] content) {
		return hmac.doFinal(content);
	}

	/*public static void main(String[] args) {
		Date start = new Date();
		byte[] secret = new byte[70];
		new java.security.SecureRandom().nextBytes(secret);
		TokenHandler tokenHandler = new TokenHandler(secret);
		System.out.println(new String(secret));
		for (int i = 0; i < 1000; i++) {
			final TokenUser user = new TokenUser();
			user.setUserName(java.util.UUID.randomUUID().toString().substring(0, 8));
			user.setExpires(new Date(
					new Date().getTime() + 10000).getTime() );
			
			user.setAuthority("CUSTOMER");
			user.setId((long) i);
			final String token = tokenHandler.createTokenForUser(new User(user));
			//System.out.println(token);
			final User parsedUser = tokenHandler.parseUserFromToken(token);
			if (parsedUser == null || parsedUser.getUsername() == null) {
				System.out.println("error");
			}
		}
		System.out.println(System.currentTimeMillis() - start.getTime());
	}*/

}

