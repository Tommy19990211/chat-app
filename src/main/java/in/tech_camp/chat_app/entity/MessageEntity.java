package in.tech_camp.chat_app.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class MessageEntity {
  private long id;
  private String content;
  private UserEntity user;
  private RoomEntity room;
  private String image;
  private Timestamp  createdAt;
}
