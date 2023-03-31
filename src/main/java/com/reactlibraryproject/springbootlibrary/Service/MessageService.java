package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.MessageNotFoundException;
import com.reactlibraryproject.springbootlibrary.DAO.MessageRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Message;
import com.reactlibraryproject.springbootlibrary.RequestModels.AdminQuestionRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    public void putMessage(AdminQuestionRequest adminQuestionRequest, String adminEmail) {
        Optional<Message> foundMessage = messageRepository.findById(adminQuestionRequest.getId());
        if (foundMessage.isEmpty()) {
            throw new MessageNotFoundException();
        }
        Message message = Message.builder()
         .id(foundMessage.get().getId())
         .userEmail(foundMessage.get().getUserEmail())
         .title(foundMessage.get().getTitle())
         .question(foundMessage.get().getQuestion())
         .adminEmail(adminEmail)
         .response(adminQuestionRequest.getResponse())
         .closed(true)
         .build();
        messageRepository.save(message);
    }
}
