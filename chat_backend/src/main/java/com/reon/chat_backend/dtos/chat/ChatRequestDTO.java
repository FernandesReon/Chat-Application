package com.reon.chat_backend.dtos.chat;

import com.reon.chat_backend.models.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDTO {
    private String content;
    private String sender;
    private String receiver;
    private String color;
    private LocalDateTime time;
    private MessageType type;
}
