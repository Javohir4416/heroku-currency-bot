package javokhir.dev.herokucurrencybot.controller;

import javokhir.dev.herokucurrencybot.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/telegram")
public class WebHookController {

    private final TelegramService telegramService;


    @PostMapping
    public void getUpdates(@RequestBody Update update) throws IOException {
        telegramService.getUpdates(update);
    }
}
