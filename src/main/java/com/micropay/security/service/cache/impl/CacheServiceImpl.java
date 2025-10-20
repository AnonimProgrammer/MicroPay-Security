package com.micropay.security.service.cache.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micropay.security.exception.InvalidTokenException;
import com.micropay.security.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    @Override
    public <T> T getOrPut(String cacheName, String key, TypeReference<T> type, Supplier<T> supplier) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("Cache '{}' not found. Fetching from DB.", cacheName);
            return supplier.get();
        }
        Object cached = cache.get(key, Object.class);
        if (cached != null) {
            try {
                T value = objectMapper.convertValue(cached, type);
                log.info("Cache hit for '{}' with key '{}'", cacheName, key);
                return value;
            } catch (Exception e) {
                log.warn("Failed to deserialize cache for key '{}'. Fetching from DB.", key, e);
            }
        } else {
            log.info("Cache miss for '{}' with key '{}'. Fetching from DB.", cacheName, key);
        }
        T value = supplier.get();
        cache.put(key, value);
        log.info("Value cached for '{}' with key '{}'", cacheName, key);
        return value;
    }

    private Cache getCache() {
        Cache cache = cacheManager.getCache("blacklistedTokens");
        if (cache == null) {
            log.warn("Cache '{}' not found. ", "blacklistedTokens");
        }
        return cache;
    }

    @Override
    public void checkAndBlacklist(String refreshToken) {
        Cache cache = getCache();
        String key = generateKey(refreshToken);

        if (cache.get(key) != null) {
            throw new InvalidTokenException("Token blacklisted.");
        }
        cache.put(key, true);
        log.info("Token blacklisted: {} ", refreshToken);
    }

    private String generateKey(String token) {
        return "blacklist:" + token;
    }

    @Override
    public void evict(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.info("Evicted key '{}' from cache '{}'", key, cacheName);
        } else {
            log.warn("Cache '{}' not found. Cannot evict key '{}'.", cacheName, key);
        }
    }

    @Override
    public void evictAll(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("Cache '{}' cleared.", cacheName);
        } else {
            log.warn("Cache '{}' not found. Cannot clear.", cacheName);
        }
    }
}
