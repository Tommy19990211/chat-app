package in.tech_camp.chat_app.form;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.chat_app.factories.RoomFormFactory;
import in.tech_camp.chat_app.validation.ValidationPriority1;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
@SpringBootTest
public class RoomFormUnitTest {
  private RoomForm roomForm;

  private Validator validator;

  @BeforeEach
  public void setUp() {
    roomForm = RoomFormFactory.createRoom();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Nested
  class ルームを作成できる場合 {
    @Test
    public void nameの値が存在すれば作成できる () {
      Set<ConstraintViolation<RoomForm>>checkList = validator.validate(roomForm, ValidationPriority1.class);
      assertEquals(0, checkList.size());
    }
  }

  @Nested
  class ルームを作成できない場合 {
    @Test
    public void nameが空では作成できない () {
      roomForm.setName("");
      Set<ConstraintViolation<RoomForm>>checkList = validator.validate(roomForm, ValidationPriority1.class);
      assertEquals(1, checkList.size());
      assertEquals("ルーム名が空白です。", checkList.iterator().next().getMessage());
    }
  }
}
