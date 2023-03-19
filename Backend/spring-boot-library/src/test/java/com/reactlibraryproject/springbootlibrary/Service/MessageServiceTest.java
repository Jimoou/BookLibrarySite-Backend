package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.MessageRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Message;
import com.reactlibraryproject.springbootlibrary.RequestModels.AdminQuestionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("문의게시판 테스트")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class MessageServiceTest {

  @InjectMocks private MessageService messageService;

  private Message message;
  public String userEmail;

  @Captor private ArgumentCaptor<Message> messageCaptor;

  @Mock private MessageRepository messageRepository;

  @BeforeEach
  void setUp() {
    userEmail = "user@email.com";
    message =
        Message.builder().id(1L).userEmail(userEmail).title("Title").question("question").build();
  }

  @Test
  @DisplayName("유저 - 문의하기")
  void postMessage() {
    // Given

    // When
    messageService.postMessage(message, userEmail);

    // Then
    verify(messageRepository).save(message);
  }

  @Test
  @DisplayName("관리자 - 응답하기")
  void putMessage() throws Exception {
    // Given
    String adminEmail = "admin@email.com";
    String response = "response";
    AdminQuestionRequest adminQuestionRequest = new AdminQuestionRequest(1L, response);
    given(messageRepository.findById(adminQuestionRequest.getId()))
        .willReturn(Optional.of(message));

    // When
    messageService.putMessage(adminQuestionRequest, adminEmail);

    // Then
    messageCaptor = ArgumentCaptor.forClass(Message.class);
    verify(messageRepository).save(messageCaptor.capture());
    Message savedMessage = messageCaptor.getValue();
    assertEquals(adminEmail, savedMessage.getAdminEmail());
    assertEquals(response, savedMessage.getResponse());
    assertTrue(savedMessage.isClosed());
  }
}
