package com.reon.chat_backend.mapper;

import com.reon.chat_backend.dtos.chat.ChatRequestDTO;
import com.reon.chat_backend.dtos.chat.ChatResponseDTO;
import com.reon.chat_backend.models.ChatMessage;

public class ChatMapper {
    public static ChatMessage toEntity(ChatRequestDTO dto) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(dto.getContent());
        chatMessage.setSender(dto.getSender());
        chatMessage.setReceiver(dto.getReceiver());
        chatMessage.setColor(dto.getColor());
        chatMessage.setTimestamp(dto.getTime());
        chatMessage.setType(dto.getType());
        return chatMessage;
    }

    public static ChatResponseDTO responseToUser(ChatMessage chatMessage) {
        ChatResponseDTO dto = new ChatResponseDTO();
        dto.setId(chatMessage.getId());
        dto.setContent(chatMessage.getContent());
        dto.setSender(chatMessage.getSender());
        dto.setReceiver(chatMessage.getReceiver());
        dto.setColor(chatMessage.getColor());
        dto.setTime(chatMessage.getTimestamp());
        dto.setType(chatMessage.getType());
        return dto;
    }
}
