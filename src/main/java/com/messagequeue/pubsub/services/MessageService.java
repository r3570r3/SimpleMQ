package com.messagequeue.pubsub.services;

import com.messagequeue.pubsub.entities.Message;
import com.messagequeue.pubsub.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepo;

    /**
     *
     * @param payload
     * @return
     */
    public Message createMessage(String payload) {
        return messageRepo.save(new Message(payload));
    }

    /**
     *
     * @return
     */
    public Optional<Message> readMessage() {
        List<Message> messages = messageRepo.findAll();
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.get(0));
    }

    /**
     *
     */
    public void deleteMessage() {
        messageRepo.delete(messageRepo.findAll().get(0));
    }
}
