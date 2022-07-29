package javokhir.dev.herokucurrencybot.service;

import javokhir.dev.herokucurrencybot.constants.RestConstants;
import javokhir.dev.herokucurrencybot.entity.User;
import javokhir.dev.herokucurrencybot.feign.TelegramFeign;
import javokhir.dev.herokucurrencybot.repo.UserRepo;
import javokhir.dev.herokucurrencybot.repo.UserStateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static javokhir.dev.herokucurrencybot.payload.enums.UserStateNames.*;

@Service
@RequiredArgsConstructor
public class AdminService {

   private final UserRepo userRepo;
   private final UserStateRepo userStateRepo;
   private final TelegramFeign telegramFeign;

   private final ReplyMarkup replyMarkup;
   private final UserService userService;

    public void throwToAdminCabinet(Update update){
        User user = userService.getUserFromUpdate(update);
        if(update.getMessage().getText().equals(RestConstants.PASSWORD)) {
            user.setState(userStateRepo.findByUserState(THROW_TO_ADMIN_CABINET));
            user=userRepo.save(user);
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), "Salom admin !!! ");
            sendMessage.setReplyMarkup(replyMarkup.inlineMarkup(user));
            telegramFeign.sendMessageToUser(sendMessage);
        }
        else {
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), "Parol xato ");
            user.setState(userStateRepo.findByUserState(ENTERED_WRONG_PASSWORD_FOR_ADMIN));
            user=userRepo.save(user);
            sendMessage.setReplyMarkup(replyMarkup.inlineMarkup(user));
            telegramFeign.sendMessageToUser(sendMessage);
        }
    }

    public void sendMessageToUsers(String text,Update update) {
        User user1 = userService.getUserFromUpdate(update);
        List<User> userRepoAll = userRepo.findAll();
        for (User user : userRepoAll) {
            if(!user.getId().equals(user1.getId())) {
                SendMessage sendMessage = new SendMessage(user.getId().toString(), text);
                telegramFeign.sendMessageToUser(sendMessage);
            }
        }
    }

    public void sendMessageToAdmin(Update update) {
        User userFromUpdate = userService.getUserFromUpdate(update);
        userFromUpdate.setState(userStateRepo.findByUserState(SEND_MESSAGE_TO_USERS));
        User save = userRepo.save(userFromUpdate);
        SendMessage sendMessage = new SendMessage(save.getId().toString(),
                "O'z xabaringizni kiriting ✍️: ");
        telegramFeign.sendMessageToUser(sendMessage);
    }

    public void checkForAdmin(Update update) {
        User userFromUpdate = userService.getUserFromUpdate(update);
        userFromUpdate.setState(userStateRepo.findByUserState(ENTER_PASSWORD_FOR_ADMIN));
        User save = userRepo.save(userFromUpdate);
        SendMessage sendMessage = new SendMessage(save.getId().toString(),
                "Parolni kiriting ✍️: ");
        telegramFeign.sendMessageToUser(sendMessage);
    }

    public void   reenterPasswordOrMainMenu(Update update) {
        String data = update.getCallbackQuery().getData();
        if(data.equals("REENTER_PASSWORD")){
            checkForAdmin(update);
        }
        else {
            userService.showMenu(update);
        }
    }

}
