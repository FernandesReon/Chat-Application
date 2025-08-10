package com.reon.chat_backend.listener;

import com.reon.chat_backend.dtos.chat.ChatResponseDTO;
import com.reon.chat_backend.exceptions.UserNotFoundException;
import com.reon.chat_backend.models.MessageType;
import com.reon.chat_backend.models.User;
import com.reon.chat_backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@Component
public class WebSocketEventListener {
    private final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String email = (String) headerAccessor.getSessionAttributes().get("email");

        if (email != null) {
            log.info("WebSocketEventListener :: Disconnected from email " + email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email" + email));
            user.setOnline(false);
            userRepository.save(user);

            ChatResponseDTO response = new ChatResponseDTO();
            response.setSender(email);
            response.setContent(user.getName() + "has left the chat");
            response.setType(MessageType.LEAVE);
            response.setTime(LocalDateTime.now());

            messagingTemplate.convertAndSend("/topic/public", response);
        }
    }
}