package com.reon.chat_backend.controllers;

import com.reon.chat_backend.dtos.chat.ChatRequestDTO;
import com.reon.chat_backend.dtos.chat.ChatResponseDTO;
import com.reon.chat_backend.services.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatResponseDTO addUser(@Payload ChatRequestDTO chatRequestDTO, SimpMessageHeaderAccessor headerAccessor) {
        log.info("ChatController :: Received add user request");
        return chatService.addUser(chatRequestDTO, headerAccessor);
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatResponseDTO sendMessage(@Payload ChatRequestDTO chatRequestDTO) {
        log.info("ChatController :: Received group chat messages");
        return chatService.sendGroupChatMessage(chatRequestDTO);
    }

    // private chat
    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatRequestDTO chatRequestDTO, SimpMessageHeaderAccessor headerAccessor) {
        log.info("ChatController :: Received private chat message");
        chatService.sendPrivateMessage(chatRequestDTO);
    }
}
