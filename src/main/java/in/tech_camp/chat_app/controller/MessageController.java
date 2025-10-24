package in.tech_camp.chat_app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;



import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.chat_app.ImageUrl;
import in.tech_camp.chat_app.custom_user.CustomUserDetail;
import in.tech_camp.chat_app.entity.MessageEntity;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.MessageForm;
import in.tech_camp.chat_app.repository.MessageRepository;
import in.tech_camp.chat_app.repository.RoomRepository;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.validation.ValidationOrder;
import lombok.AllArgsConstructor;



@Controller
@AllArgsConstructor
public class MessageController {
  private final UserRepository userRepository; 
  private final RoomUserRepository roomUserRepository;
  private final RoomRepository roomRepository;
  private final MessageRepository messageRepository;
  private final ImageUrl imageUrl;

  @GetMapping("/rooms/{roomId}/messages")
  public String showMessages(@PathVariable("roomId") Integer roomId, @AuthenticationPrincipal CustomUserDetail currentUser, Model model) {
    UserEntity userEntity = userRepository.findById(currentUser.getId());
    List<RoomUserEntity>roomUserEntity = roomUserRepository.findByUserId(currentUser.getId());
    List<RoomEntity>roomEntity=roomUserEntity.stream()
    .map(RoomUserEntity :: getRoom)
    .collect(Collectors.toList());

    // メッセージ取得処理
    List<MessageEntity>messages = messageRepository.findByRoomId(roomId);
    model.addAttribute("user", userEntity);
    model.addAttribute("rooms", roomEntity);
    model.addAttribute("messageForm", new MessageForm());
    model.addAttribute("roomId",roomId);
    //messagesリストをviewにバインド
    model.addAttribute("messages",messages);

    model.addAttribute("room",roomRepository.findById(roomId));
    return "messages/index";
  }

  @PostMapping("/rooms/{roomId}/message")
  public String postMethodName(@PathVariable Integer roomId, @AuthenticationPrincipal CustomUserDetail currentUser,  @ModelAttribute @Validated(ValidationOrder.class) MessageForm messageForm
  ,BindingResult bindingResult) {

    messageForm.validationMessage(bindingResult);

    if(bindingResult.hasErrors()){
      return "redirect:/rooms/"+ roomId +"/messages";
    }

    // メッセージ登録事前処理
    UserEntity userEntity = userRepository.findById(currentUser.getId());
    RoomEntity roomEntity = roomRepository.findById(roomId);
    // メッセージ登録処理
    MessageEntity messageEntity = new MessageEntity();

    //イメージ登録処理
    MultipartFile imageFile = messageForm.getImage();
    if(imageFile != null && !imageFile.isEmpty()){ //StringUtils
      try {
        String uploadDir = imageUrl.getImageUrl();
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + imageFile.getOriginalFilename();
        Path imagePath = Paths.get(uploadDir,fileName);
        Files.copy(imageFile.getInputStream(), imagePath);
        messageEntity.setImage("/uploads/"+ fileName);

      } catch (IOException e) {
        System.out.println("エラー：" + e);
        return "redirect:/rooms/" + roomId + "/messages";
      }
    }


    messageEntity.setUser(userEntity);
    messageEntity.setRoom(roomEntity);
    messageEntity.setContent(messageForm.getContent());
    try {
      messageRepository.insert(messageEntity);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      // エラー起きたときは遷移前のmessageFormを復元
    //  model.addAttribute("messageForm",messageForm);
    }

    //遷移前処理
    //サイドバー用userバインド
    //model.addAttribute("user",userEntity);

    //サイドバー用roomsバインド
    // List<RoomEntity>rooms =roomUserRepository.findByUserId(currentUser.getId()).stream()
    // .map(RoomUserEntity :: getRoom)
    // .collect(Collectors.toList());
    // model.addAttribute("rooms",rooms);

    // // サイドバー用ルームIDバインド
    // model.addAttribute("roomId",roomId);

    return "redirect:/rooms/"+ roomId +"/messages";
  }
  
}
