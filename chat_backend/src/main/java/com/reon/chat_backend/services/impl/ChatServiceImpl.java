package com.reon.chat_backend.services.impl;

import com.reon.chat_backend.dtos.chat.ChatRequestDTO;
import com.reon.chat_backend.dtos.chat.ChatResponseDTO;
import com.reon.chat_backend.exceptions.UserNotFoundException;
import com.reon.chat_backend.mapper.ChatMapper;
import com.reon.chat_backend.models.ChatMessage;
import com.reon.chat_backend.models.MessageType;
import com.reon.chat_backend.models.User;
import com.reon.chat_backend.repositories.ChatMessageRepository;
import com.reon.chat_backend.repositories.UserRepository;
import com.reon.chat_backend.services.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class ChatServiceImpl implements ChatService {
    private final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatServiceImpl(UserRepository userRepository, ChatMessageRepository chatMessageRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public ChatResponseDTO addUser(ChatRequestDTO chatRequestDTO, SimpMessageHeaderAccessor headerAccessor) {
        log.info("ChatService :: Adding user {}", chatRequestDTO.getSender());
        String email = chatRequestDTO.getSender();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: "+ email));
        user.setOnline(true);
        userRepository.save(user);

        headerAccessor.getSessionAttributes().put("email", email);
        ChatResponseDTO response = new ChatResponseDTO();
        response.setSender(email);
        response.setContent(user.getName() + " has joined the chat");
        response.setType(MessageType.JOIN);
        response.setTime(LocalDateTime.now());

        return response;
    }

    @Override
    public ChatResponseDTO sendGroupChatMessage(ChatRequestDTO chatRequestDTO) {
        log.info("ChatService :: Sending group message from {}", chatRequestDTO.getSender());
        String senderEmail = chatRequestDTO.getSender();

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("Sender not found with email " + senderEmail));

        ChatMessage message = ChatMapper.toEntity(chatRequestDTO);
        message.setType(MessageType.CHAT);
        message.setTimestamp(LocalDateTime.now());

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ChatMapper.responseToUser(savedMessage);
    }

    @Override
    public void sendPrivateMessage(ChatRequestDTO chatRequestDTO) {
        log.info("ChatService :: Sending private message from {} to {}",
                chatRequestDTO.getSender(), chatRequestDTO.getReceiver());

        String senderEmail = chatRequestDTO.getSender();
        String receiverEmail = chatRequestDTO.getReceiver();

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("Sender not found with email " + senderEmail));
        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new UserNotFoundException("Receiver not found with email " + receiverEmail));

        ChatMessage message = ChatMapper.toEntity(chatRequestDTO);
        message.setType(MessageType.PRIVATE_MESSAGE);
        message.setTimestamp(LocalDateTime.now());

        ChatMessage savedMessage = chatMessageRepository.save(message);
        ChatResponseDTO response = ChatMapper.responseToUser(savedMessage);

        messagingTemplate.convertAndSendToUser(receiverEmail, "/queue/private", response);
        messagingTemplate.convertAndSendToUser(senderEmail, "/queue/private", response);
    }

    @Override
    public List<ChatResponseDTO> getGroupChatMessages() {
        log.info("ChatService :: Fetching group chat history");
        return chatMessageRepository.findByReceiverIsNullOrderByTimestampAsc()
                .stream()
                .map(ChatMapper::responseToUser)
                .toList();
    }

    @Override
    public List<ChatResponseDTO> getPrivateChatMessages(String senderEmail, String receiverEmail) {
        log.info("ChatService :: Fetching private chat history between {} and {}", senderEmail, receiverEmail);
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException("Sender not found with email " + senderEmail));
        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new UserNotFoundException("Receiver not found with email " + receiverEmail));

        return chatMessageRepository
                .findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                        senderEmail, receiverEmail, receiverEmail, senderEmail)
                .stream()
                .map(ChatMapper::responseToUser)
                .toList();
    }
}