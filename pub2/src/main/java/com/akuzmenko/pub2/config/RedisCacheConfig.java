package com.akuzmenko.pub2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig {

    public static final String PUB_CACHE = "pub-cache";

    @Value("${cache.host}")
    private String host;

    @Value("${cache.port}")
    private Integer port;

    @Value("${cache.password}")
    private String password;

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration(PUB_CACHE,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10)));
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        var config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(password);
        return new JedisConnectionFactory(config);
    }
}
