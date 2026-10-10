package com.infy.utility;

import org.springframework.stereotype.Component;

import com.infy.entity.User;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	private final Key key;
	private final long expirationMs;

	public JwtUtil(@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration-ms}") long expirationMs) {
		// secret should be base64-safe; for simplicity we use raw bytes of the provided
		// string
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.expirationMs = expirationMs;
	}

	public String generateToken(User user) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMs);

		return Jwts.builder()
				.setSubject(String.valueOf(user.getUserId()))
				.claim("email", user.getEmail())
				.setIssuedAt(now)
				.setExpiration(exp)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	public Jws<Claims> validateToken(String token) throws JwtException {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	}

	public Integer getUserIdFromToken(String token) {
		Claims claims = validateToken(token).getBody();
		return Integer.parseInt(claims.getSubject());
	}
}
