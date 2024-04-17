package com.akuzmenko.pub1.controller;

import com.akuzmenko.core.TestMessage;
import com.akuzmenko.pub1.service.StompPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/stomp/publish")
@RequiredArgsConstructor
public class TestController {

    private final StompPublishService publishService;

    @PostMapping("topic")
    public void publishToTopic(@RequestParam String topic, @RequestParam String message) {
        publishService.publishToTopic(topic, message);
    }

    @PostMapping("queue")
    public void publishToQueue(@RequestParam String message, Principal principal) {
//        publishService.publishToInstances(message, principal.getName());
//        publishService.publishToInstance(message, principal.getName());
        publishService.publishToUser(new TestMessage(principal.getName(), message));
    }
}
