package com.infy.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CaffeineCache {
	public CacheManager caffeineCacheManager() {
		CaffeineCacheManager cacheManager=new CaffeineCacheManager("transactions");
		cacheManager.setCaffeine(caffeineCacheBuilder());
		return cacheManager;
	}
	private Caffeine<Object,Object> caffeineCacheBuilder(){
		return Caffeine.newBuilder().expireAfterWrite(10,TimeUnit.MINUTES);
	}
}
