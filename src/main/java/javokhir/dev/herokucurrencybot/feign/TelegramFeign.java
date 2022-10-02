package javokhir.dev.herokucurrencybot.feign;

import javokhir.dev.herokucurrencybot.constants.RestConstants;
import javokhir.dev.herokucurrencybot.payload.ResultTelegram;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@FeignClient(url = RestConstants.TELEGRAM_BASE_URL,name = "TelegramFeign")
public interface TelegramFeign {



    @PostMapping("/bot"+RestConstants.BOT_TOKEN+"/sendMessage")

    ResultTelegram sendMessageToUser(@RequestBody  SendMessage sendMessage);
}
