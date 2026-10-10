package com.infy.security;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * Simple in-memory login attempt tracker.
 * 
 * Blocks for BLOCK_DURATION_SECONDS after MAX_ATTEMPTS failed attempts.
 * 
 * Suitable for single-instance dev. For production, use Redis.
 * 
 */

@Component
public class LoginAttemptService {

	private static final int MAX_ATTEMPTS = 3;
	private static final long BLOCK_DURATION_SECONDS = 60;

	private static final class Record {
		int attempts;
		Instant blockedUntil;
	}

	private final Map<String, Record> cache = new ConcurrentHashMap<>();

	public void loginSucceeded(String key) {
		cache.remove(key);
	}

	public void loginFailed(String key) {
		Record r = cache.computeIfAbsent(key, k -> new Record());
		r.attempts = r.attempts + 1;
		if (r.attempts >= MAX_ATTEMPTS) {
			r.blockedUntil = Instant.now().plusSeconds(BLOCK_DURATION_SECONDS);
			r.attempts = 0; // reset attempts after block
		}
	}

	public boolean isBlocked(String key) {
		Record r = cache.get(key);
		if (r == null)
			return false;
		if (r.blockedUntil == null)
			return false;
		if (Instant.now().isBefore(r.blockedUntil))
			return true;

		// block expired
		cache.remove(key);
		return false;
	}

}
