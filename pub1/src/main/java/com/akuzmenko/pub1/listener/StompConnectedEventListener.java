package com.akuzmenko.pub1.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.util.Objects;

@Component
@Slf4j
public class StompConnectedEventListener implements ApplicationListener<SessionConnectedEvent> {

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("New STOMP client session {} is created for User {}, ", accessor.getSessionId(),
                Objects.requireNonNull(accessor.getUser()).getName());
    }
}
