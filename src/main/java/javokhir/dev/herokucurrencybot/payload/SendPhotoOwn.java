package javokhir.dev.herokucurrencybot.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendPhotoOwn {
    @JsonProperty(value = "chat_id")
    private String chatId;
    private String caption;
    private String photo;
}
