package br.com.labs.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@EnableScheduling
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    public static final String AUTHOR_MUSICS_CACHE = "authorMusics";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.info("Configuring Redis Cache Manager");

        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Specific cache configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Cache for author musics - TTL of 5 minutes
        cacheConfigurations.put(AUTHOR_MUSICS_CACHE, RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues());

        log.info("Redis Cache configured with {} custom caches", cacheConfigurations.size());

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    /**
     * Scheduled task to clear author musics cache every hour.
     * This is useful for ensuring data consistency in case of external database changes.
     */
    @CacheEvict(allEntries = true, cacheNames = {AUTHOR_MUSICS_CACHE})
    @Scheduled(fixedDelay = 3600000) // 1 hour
    public void evictAuthorMusicsCache() {
        log.info("Evicting all entries from {} cache", AUTHOR_MUSICS_CACHE);
    }
}
