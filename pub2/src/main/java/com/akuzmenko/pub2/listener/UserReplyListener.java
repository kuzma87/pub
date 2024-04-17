package com.akuzmenko.pub2.listener;

import com.akuzmenko.core.TestMessage;
import com.akuzmenko.pub2.service.StompPublishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserReplyListener {

    private final StompPublishService stompPublishService;

    @RabbitListener(queues = "#{fQueue.getName()}")
    public void receiveMessageFromFanout1(TestMessage message) {
        log.info("Received fanout message: {}", message);
        stompPublishService.publishToUser(message);
    }

    @RabbitListener(queues = "#{topicQueue.getName()}")
    public void receiveMessageFromTopicQueue(TestMessage message) {
        log.info("Received topic message: {}", message);
        stompPublishService.publishToUser(message);
    }
}
