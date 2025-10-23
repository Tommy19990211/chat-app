package in.tech_camp.chat_app.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.chat_app.entity.RoomEntity;

@Mapper
public interface  RoomRepository {
  @Insert("insert into rooms(name)values(#{name})")
  @Options(useGeneratedKeys=true,keyProperty="id")
  void insert(RoomEntity roomEntity);

  @Select("select * from rooms where id = #{roomId}")
  RoomEntity findById(Integer roomId);

  @Delete("delete from rooms where id = #{roomId}")
  void deleteById(Integer roomId);

}
