package in.tech_camp.chat_app.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import in.tech_camp.chat_app.entity.RoomEntity;

@Mapper
public interface  RoomRepository {
  @Insert("insert into rooms(name)values(#{name})")
  @Options(useGeneratedKeys=true,keyProperty="id")
  void insert(RoomEntity roomEntity);
}
