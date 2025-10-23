package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import in.tech_camp.chat_app.custom_user.CustomUserDetail;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import in.tech_camp.chat_app.repository.UserRepository;
import lombok.AllArgsConstructor;



@Controller
@AllArgsConstructor
public class MessageController {
  private final UserRepository userRepository; 
  private final RoomUserRepository roomUserRepository;
  @GetMapping("message")
  public String showMessages(@AuthenticationPrincipal CustomUserDetail currentUser, Model model) {
    UserEntity userEntity = userRepository.findById(currentUser.getId());
    List<RoomUserEntity>roomUserEntity = roomUserRepository.findByUserId(currentUser.getId());
    List<RoomEntity>roomEntity=roomUserEntity.stream()
    .map(RoomUserEntity :: getRoom)
    .collect(Collectors.toList());

    model.addAttribute("user", userEntity);
    model.addAttribute("rooms", roomEntity);

    return "messages/index";
  }

}
