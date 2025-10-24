package in.tech_camp.chat_app.form;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import in.tech_camp.chat_app.factories.MessageFormFactory;

@ActiveProfiles("test")
@SpringBootTest
public class MessageFormUnitTest {
  private MessageForm messageForm;
  private BindingResult bindingResult;

  @BeforeEach
  public void setUp() {
    messageForm = MessageFormFactory.createMessage();
    bindingResult = Mockito.mock(BindingResult.class);
  }

  @Nested
  class メッセージが投稿できる場合 {
    @Test
    public void contentとimageが存在していれば保存できる () {
      messageForm.validationMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(),anyString(),anyString());
    }

    @Test
    public void contentが空でも保存できる () {
      messageForm.setContent("");
      messageForm.validationMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(),anyString(),anyString());
    }

    @Test
    public void contentがnullでも保存できる () {
      messageForm.setContent(null);
      messageForm.validationMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(),anyString(),anyString());

    }

    @Test
    public void imageが空でも保存できる () {
      messageForm.setImage(new MockMultipartFile("image",  "empty.jpg", "image/jpeg", new byte[0]));
      messageForm.validationMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(),anyString(),anyString());
    }

    @Test
    public void imageがnullでも保存できる () {
      messageForm.setImage(null);
      messageForm.validationMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(),anyString(),anyString());

    }
  }

  @Nested
  class メッセージが投稿できない場合 {
    @Test
    public void contentが空かつimageが空ファイルだと保存できない() {
      messageForm.setImage(new MockMultipartFile("image", "empty.jpg", "image.jpg",new byte[0]));
      messageForm.setContent("");
      messageForm.validationMessage(bindingResult);
      verify(bindingResult).rejectValue("Content", "Error.Message", "Please enter either content or image");
    }

    @Test
    public void contentが空かつimageがnullだと保存できない() {
      messageForm.setImage(null);
      messageForm.setContent("");
      messageForm.validationMessage(bindingResult);
      verify(bindingResult).rejectValue("Content", "Error.Message", "Please enter either content or image");

    }

    @Test
    public void contentがnullかつimageが空ファイルだと保存できない() {
      messageForm.setImage(new MockMultipartFile("image", "empty.jpg", "image.jpg",new byte[0]));
      messageForm.setContent(null);
      messageForm.validationMessage(bindingResult);
      verify(bindingResult).rejectValue("Content", "Error.Message", "Please enter either content or image");
    }

    @Test
    public void contentがnullかつimageがnullだと保存できない() {
      messageForm.setImage(null);
      messageForm.setContent(null);
      messageForm.validationMessage(bindingResult);
      verify(bindingResult).rejectValue("Content", "Error.Message", "Please enter either content or image");

    }
  }
}