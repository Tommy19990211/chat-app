package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.chat_app.custom_user.CustomUserDetail;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.RoomForm;
import in.tech_camp.chat_app.repository.RoomRepository;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import in.tech_camp.chat_app.validation.ValidationOrder;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class RoomController {

  private final UserRepository userRepository;
  private final RoomRepository roomRepository;
  private final RoomUserRepository roomUserRepository;
  


  @GetMapping("/")
  public String showMessages(@AuthenticationPrincipal CustomUserDetail currentUser, Model model) {
    UserEntity userEntity = userRepository.findById(currentUser.getId());
    List<RoomUserEntity>roomUserEntity = roomUserRepository.findByUserId(currentUser.getId());
    List<RoomEntity>roomEntity=roomUserEntity.stream()
    .map(RoomUserEntity :: getRoom)
    .collect(Collectors.toList());

    model.addAttribute("user", userEntity);
    model.addAttribute("rooms", roomEntity);

    return "rooms/index";
  }

  @GetMapping("/rooms/new")
  public String getMethodName(@AuthenticationPrincipal CustomUserDetail currentUser, Model model) {
    List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
    model.addAttribute("users", users);
    model.addAttribute("roomForm", new RoomForm());
    return "rooms/new";
  }

  @PostMapping("/rooms")
  public String createRooms(@ModelAttribute("roomForm") @Validated(ValidationOrder.class) RoomForm roomForm, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetail currentUser, Model model) {
    System.out.println("roomForm" + roomForm);

    if (bindingResult.hasErrors()) {
      List<String>errorMessages = bindingResult.getAllErrors().stream()
        .map(DefaultMessageSourceResolvable :: getDefaultMessage)
        .collect(Collectors.toList());
      List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
      model.addAttribute("users", users);
      model.addAttribute("roomForm", roomForm);
      model.addAttribute("errorMessages", errorMessages);
      return "rooms/new";
    }

    // roomsレコード作成
    RoomEntity roomEntity=new RoomEntity();
    roomEntity.setName(roomForm.getName());
    try {
        roomRepository.insert(roomEntity);
    } catch (Exception e) {
      List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
      model.addAttribute("users", users);
      model.addAttribute("roomForm", new RoomForm());
      return "rooms/new";
    }

    // room_usersレコード作成
    List<Integer>memberIds=roomForm.getMemberIds();
    for(Integer userId : memberIds){
      UserEntity userEntity = userRepository.findById(userId);
      RoomUserEntity roomUserEntity = new RoomUserEntity();
      roomUserEntity.setRoom(roomEntity);
      roomUserEntity.setUser(userEntity);
      try {
        roomUserRepository.insert(roomUserEntity);
      } catch (Exception e) {
        System.out.println("エラー：" + e);
        List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
        model.addAttribute("users", users);
        model.addAttribute("roomForm", new RoomForm());
        return "rooms/new";
      }
    }
    return "redirect:/";
  }

  @GetMapping("/rooms/{roomId}/delete")
  public String deleteRoom(@PathVariable Integer roomId) {
      roomRepository.deleteById(roomId);
      return "redirect:/";
  }
  
}
