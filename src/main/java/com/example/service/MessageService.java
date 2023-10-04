package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    private MessageRepository messageRepository;
    private AccountRepository accountRepository;
    public static final int MAX_MESSAGE_LENGTH = 254;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public Message createMessage(Message message) {
        if (message.getMessage_text().isEmpty() || message.getMessage_text().length() > MAX_MESSAGE_LENGTH) {
            return null;
        }
        if (this.accountRepository.findById(message.getPosted_by()) == null) {
            return null;
        }
        return this.messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return this.messageRepository.findAll();
    }

    public Message getMessageById(int message_id) {
        Optional<Message> messageOpt = this.messageRepository.findById(message_id);
        if (messageOpt.isPresent()) {
            return messageOpt.get();
        }
        return null;
    }
    
    public int deleteMessageById(int message_id) {
        Message message = this.getMessageById(message_id);
        if (message == null) {
            return 0;
        }
        this.messageRepository.deleteById(message_id);
        return 1;
    }

    public int updateMessageById(int message_id, String message_text) {
        Message message = this.getMessageById(message_id);
        if (message == null) {
            throw new RuntimeException();
        }
        if (message_text.isEmpty() || message_text.length() > MAX_MESSAGE_LENGTH) {
            throw new RuntimeException();
        }
        message.setMessage_text(message_text);
        this.messageRepository.save(message);
        return 1;
    }

    public List<Message> getMessagesByPostedBy(int posted_by)  {
        return this.messageRepository.findMessagesByPosted_by(posted_by);
    }

}
