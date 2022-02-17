package com.onlyoffice.registry.config;

import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableTransactionManagement
@EnableCaching
@Slf4j
public class ApplicationConfiguration {
    @Value("${spring.jpa.caching.maxSize}")
    private int maxSize;
    @Value("${spring.jpa.caching.expiresAfter}")
    private int expiresAfter;
    @Bean("registryCacheManager")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(String name) {
                return new ConcurrentMapCache(
                        name,
                        CacheBuilder.newBuilder()
                                .expireAfterWrite(expiresAfter, TimeUnit.SECONDS)
                                .maximumSize(maxSize)
                                .build().asMap(),
                        false);
            }
        };
    }
}
