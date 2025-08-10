package com.reon.chat_backend.services;

import com.reon.chat_backend.dtos.chat.ChatRequestDTO;
import com.reon.chat_backend.dtos.chat.ChatResponseDTO;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.List;

public interface ChatService {
    ChatResponseDTO addUser(ChatRequestDTO chatRequestDTO, SimpMessageHeaderAccessor headerAccessor);

    ChatResponseDTO sendGroupChatMessage(ChatRequestDTO chatRequestDTO);
    void sendPrivateMessage(ChatRequestDTO chatRequestDTO);

    List<ChatResponseDTO> getGroupChatMessages();
    List<ChatResponseDTO> getPrivateChatMessages(String senderEmail, String receiverEmail);
}
