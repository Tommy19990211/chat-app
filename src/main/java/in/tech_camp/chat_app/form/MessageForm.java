package in.tech_camp.chat_app.form;

import java.util.function.BinaryOperator;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.chat_app.validation.ValidationPriority1;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageForm {
  //@NotBlank(groups=ValidationPriority1.class)
  private String content;
  private MultipartFile image;

  public void validationMessage(BindingResult result){
    if((content == null || content.isEmpty()) && (image == null || image.isEmpty())){
      result.rejectValue("Content", "Error.Message", "Please enter either content or image");
    }
  }
}
