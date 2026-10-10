package com.infy.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.infy.entity.User;
import com.infy.repository.UserRepository;
import com.infy.utility.JwtUtil;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			try {
				Integer userId = jwtUtil.getUserIdFromToken(token);
				Optional<User> maybeUser = userRepository.findById(userId);
				if(maybeUser.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
					User user = maybeUser.get();
					
					var auth = new UsernamePasswordAuthenticationToken(user.getUserId(), null, List.of());
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
 			}catch (JwtException | IllegalArgumentException e) {
 				// Ignored
			}
		}
		filterChain.doFilter(request, response);
	}
}
