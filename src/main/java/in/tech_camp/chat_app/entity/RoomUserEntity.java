package in.tech_camp.chat_app.entity;

import lombok.Data;

@Data
public class RoomUserEntity {
  private Integer id;
  private UserEntity user;
  private RoomEntity room;
}
