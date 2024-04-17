package com.akuzmenko.pub1.service;

import com.akuzmenko.core.TestMessage;
import com.akuzmenko.pub1.config.RedisCacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StompPublishService {

    public static final String SEND_TO_USER = "/exchange/amq.direct/reply";

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final Cache subscriptionCache;

    public StompPublishService(SimpMessagingTemplate simpMessagingTemplate,
                               RabbitTemplate rabbitTemplate, CacheManager cacheManager) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.subscriptionCache = cacheManager.getCache(RedisCacheConfig.PUB_CACHE);
    }

    public void publishToTopic(String topic, String message) {
        log.info("Sending message to topic {}", topic);
        simpMessagingTemplate.convertAndSend(topic, new TestMessage("", message));
    }

    public void publishToInstances(String message, String userId) {
        log.info("Sending message to instances");
        rabbitTemplate.convertAndSend("pub.fanout", "", new TestMessage(userId, message));
    }

    public void publishToInstance(String message, String userId) {
        log.info("Sending message to instance by user session");
        var instanceKey = subscriptionCache.get(userId, String.class);
        if (instanceKey != null) {
            log.info("Instance {} for user {} found", instanceKey, userId);
            rabbitTemplate.convertAndSend("pub.topic", instanceKey, new TestMessage(userId, message));
        }
    }

    public void publishToUser(TestMessage message) {
        log.info("Sending message to user");
        simpMessagingTemplate.convertAndSendToUser(message.getUserId(), SEND_TO_USER, message);
    }
}
