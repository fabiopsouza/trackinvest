package br.com.trackinvest.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;


@Configuration
@EnableCaching
@SuppressWarnings({"rawtypes", "unchecked"})
public class CacheConfig 
{
	@Bean
    public Caffeine caffeine() {
		return Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.DAYS);
    }
	
	@Bean
	public CacheManager cacheManager(Caffeine caffeine) {
	    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
	    cacheManager.setCaffeine(caffeine);
	    return cacheManager;
	}
}
