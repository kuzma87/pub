package com.akuzmenko.pub2.listener;

import com.akuzmenko.pub2.config.RedisCacheConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Objects;

@Component
@Slf4j
public class StompUnsubscribeEventListener implements ApplicationListener<SessionUnsubscribeEvent> {

    private static final String REPLY_SUBSCRIPTION = "/user/exchange/amq.direct/reply";

    private final CacheManager cacheManager;

    private Cache subscriptionCache;

    public StompUnsubscribeEventListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void init() {
        subscriptionCache = cacheManager.getCache(RedisCacheConfig.PUB_CACHE);
    }

    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        var userId = Objects.requireNonNull(accessor.getUser()).getName();
        log.info("Unsubscribed. Subscription {} for User {} and Session {}. Destination: {}",
                accessor.getSubscriptionId(), userId,
                accessor.getSessionId(), accessor.getDestination());

        if (Objects.equals(accessor.getDestination(), REPLY_SUBSCRIPTION)) {
            subscriptionCache.evict(userId);
        }
    }
}
