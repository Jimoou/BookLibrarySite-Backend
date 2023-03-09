package com.reactlibraryproject.springbootlibrary.Service;

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

    public void putMessage(AdminQuestionRequest adminQuestionRequest, String useremail) throws Exception {
        Optional<Message> findedMessage = messageRepository.findById(adminQuestionRequest.getId());
        if (findedMessage.isEmpty()) {
            throw new Exception("Message not found");
        }
        Message message = Message.builder()
         .adminEmail(useremail)
         .response(adminQuestionRequest.getResponse())
         .closed(true)
         .build();
        messageRepository.save(message);
    }
}
