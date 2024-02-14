package com.stephen.login.listener;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.stephen.login.entity.User;
import com.stephen.login.exceptions.ResourceNotFoundException;
import com.stephen.login.repository.UserRepository;

@Component
public class MessageListener {

    private UserRepository userRepository;

    private static final Logger log = LogManager.getLogger(MessageListener.class);

    public MessageListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //@JmsListener(destination = Constants.USER_MESSAGE_QUEUE, containerFactory = "jmsFactory")
    public void receiveMessage(Map<String, String> message) {
        log.info("Received <" + message + ">");
        Long id = Long.valueOf(message.get("id"));
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for the id: " + id));
        user.setMessageCount(user.getMessageCount() + 1);
        userRepository.save(user);
        log.info("Message processed...");
    }
    
}
