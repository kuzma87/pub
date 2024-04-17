package com.akuzmenko.pub2.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.Objects;

@Component
@Slf4j
public class StompConnectEventListener implements ApplicationListener<SessionConnectEvent> {

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        log.info("New STOMP client session is requested for User {}.",
                Objects.requireNonNull(accessor.getUser()).getName());
    }
}
