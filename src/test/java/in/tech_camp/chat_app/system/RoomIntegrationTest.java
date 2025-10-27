package in.tech_camp.chat_app.system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.chat_app.entity.MessageEntity;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.factories.MessageFormFactory;
import in.tech_camp.chat_app.factories.RoomFormFactory;
import in.tech_camp.chat_app.factories.UserFormFactory;
import in.tech_camp.chat_app.form.MessageForm;
import in.tech_camp.chat_app.form.RoomForm;
import in.tech_camp.chat_app.form.UserForm;
import in.tech_camp.chat_app.repository.MessageRepository;
import in.tech_camp.chat_app.repository.RoomRepository;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import in.tech_camp.chat_app.service.UserService;
import in.tech_camp.chat_app.support.LoginSupport;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class RoomIntegrationTest {
  private UserForm userForm1;
  private UserForm userForm2;
  private RoomForm roomForm;
  private MessageForm messageForm;

  private UserEntity userEntity1;
  private UserEntity userEntity2;
  private RoomEntity roomEntity;
  private MessageEntity messageEntity;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private RoomUserRepository roomUserRepository;

  @Autowired
  private MessageRepository messageRepository;

  @BeforeEach
  public void setup() {
    userForm1 = UserFormFactory.createUser();
    userEntity1 = new UserEntity();
    userEntity1.setEmail(userForm1.getEmail());
    userEntity1.setName(userForm1.getName());
    userEntity1.setPassword(userForm1.getPassword());
    userService.createUserWithEncryptedPassword(userEntity1);

    userForm2 = UserFormFactory.createUser();
    userEntity2 = new UserEntity();
    userEntity2.setEmail(userForm2.getEmail());
    userEntity2.setName(userForm2.getName());
    userEntity2.setPassword(userForm2.getPassword());
    userService.createUserWithEncryptedPassword(userEntity2);

    roomForm = RoomFormFactory.createRoom();
    roomEntity = new RoomEntity();
    roomEntity.setName(roomForm.getName());
    roomRepository.insert(roomEntity);

    RoomUserEntity roomUserEntity1 = new RoomUserEntity();
    roomUserEntity1.setRoom(roomEntity);
    roomUserEntity1.setUser(userEntity1);
    roomUserRepository.insert(roomUserEntity1);
    RoomUserEntity roomUserEntity2 = new RoomUserEntity();
    roomUserEntity2.setRoom(roomEntity);
    roomUserEntity2.setUser(userEntity2);
    roomUserRepository.insert(roomUserEntity2);

    messageForm = MessageFormFactory.createMessage();

    messageEntity = new MessageEntity();
  }


  @Test
  public void チャットルームを削除すると関連するメッセージがすべて削除されている() throws Exception {
    // サインインする
    MockHttpSession loginSession = LoginSupport.login(mockMvc, userForm1);

    // 作成したチャットルームへ遷移する
    mockMvc.perform(get("/rooms/" + roomEntity.getId() + "/messages").session(loginSession))
      .andExpect(view().name("messages/index"))
      .andExpect(status().isOk());
    // メッセージ情報を5つDBに追加する
    int addMessageCnt=5;

    for(int i = 1; i<=addMessageCnt; i++){
      messageEntity.setContent(messageForm.getContent());
      messageEntity.setImage(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + messageForm.getImage().getOriginalFilename());
      messageEntity.setUser(userEntity1);
      messageEntity.setRoom(roomEntity);
      messageRepository.insert(messageEntity);
    }
    //テスト実施前件数取得
    int beforeCnt = messageRepository.count();

    // チャットルームを削除するとトップページに遷移することを確認する
    mockMvc.perform(get("/rooms/" + roomEntity.getId() + "/delete").session(loginSession))
    .andExpect(redirectedUrl("/"))
    .andExpect(status().isFound());

    // 作成した5つのメッセージが削除されていることを確認する
    //テスト実施後件数取得
    int afterCnt = messageRepository.count();

    assertEquals(afterCnt, beforeCnt-addMessageCnt);
  }
}
