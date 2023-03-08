package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.MessageRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class MessageService {

    private MessageRepository messageRepository;

    public void postMessage(Message messageRequest, String userEmail) {
        Message message = Message.builder()
         .userEmail(userEmail)
         .title(messageRequest.getTitle())
         .question(messageRequest.getQuestion())
         .build();
        messageRepository.save(message);
    }
}
