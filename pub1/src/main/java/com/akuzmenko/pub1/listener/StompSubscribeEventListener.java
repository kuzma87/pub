package com.akuzmenko.pub1.listener;

import com.akuzmenko.pub1.config.RedisCacheConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Objects;

@Component
@Slf4j
public class StompSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private static final String REPLY_SUBSCRIPTION = "/user/exchange/amq.direct/reply";

    private final CacheManager cacheManager;
    private final String instanceName;

    private Cache subscriptionCache;

    public StompSubscribeEventListener(CacheManager cacheManager,
                                       @Value("${spring.application.name}") String instanceName) {
        this.cacheManager = cacheManager;
        this.instanceName = instanceName;
    }

    @PostConstruct
    public void init() {
        subscriptionCache = cacheManager.getCache(RedisCacheConfig.PUB_CACHE);
    }

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        var userId = Objects.requireNonNull(accessor.getUser()).getName();
        log.info("Subscribed. Subscription {} is created for User {} and Session {}. Destination: {}",
                accessor.getSubscriptionId(), userId,
                accessor.getSessionId(), accessor.getDestination());

        if (Objects.equals(accessor.getDestination(), REPLY_SUBSCRIPTION)) {
            subscriptionCache.put(userId, instanceName);
        }
    }
}
