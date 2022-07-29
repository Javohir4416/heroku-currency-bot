package javokhir.dev.herokucurrencybot.service;

import javokhir.dev.herokucurrencybot.entity.*;
import javokhir.dev.herokucurrencybot.feign.TelegramFeign;
import javokhir.dev.herokucurrencybot.repo.UserStateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

import static javokhir.dev.herokucurrencybot.payload.enums.UserStateNames.*;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final UserStateRepo userStateRepo;
    private final UserService userService;
    private final AdminService adminService;

    private final TelegramFeign telegramFeign;



    public void getUpdates(Update update) throws IOException {
        if (update.hasMessage()) {
            if (update.getMessage().hasContact()) {
                User user = userService.getUserFromUpdate(update);
                SendMessage sendMessage=new SendMessage(user.getId().toString(),"Mavjud bo'lmagan buyruq");
                telegramFeign.sendMessageToUser(sendMessage);
            }


            else if (update.getMessage().hasLocation()) {
                User user = userService.getUserFromUpdate(update);
                SendMessage sendMessage=new SendMessage(user.getId().toString(),"Mavjud bo'lmagan buyruq");
                telegramFeign.sendMessageToUser(sendMessage);
            }



            else {
                String text = update.getMessage().getText();
                if(text!=null) {
                    switch (text) {
                        case "/start":

                        case "/bosh_menu":
                            userService.showMenu(update);
                            break;
                        case "/admin":
                            adminService.checkForAdmin(update);
                            break;

                        case "/valyuta_malumot":
                            userService.sendInformationToUser(update);
                            break;
                            case "/converter":
                            userService.getCurrencyForConvert(update);
                            break;

                        case "/stats":
                            userService.stats(update);
                            break;

                        default:
                            User userFromUpdate = userService.getUserFromUpdate(update);
                            if (userFromUpdate.getState().equals(userStateRepo.findByUserState(SEND_MESSAGE_TO_USERS))) {
                                adminService.sendMessageToUsers(text, update);
                            }
                               else if (userFromUpdate.getState().equals(userStateRepo.findByUserState(ENTER_PASSWORD_FOR_ADMIN))) {
                                adminService.throwToAdminCabinet(update);
                            }
                               else if (userFromUpdate.getState().equals(userStateRepo.findByUserState(CONVERTOR))) {
                                userService.getCurrencyForConvert(update);
                            }
                               else if (userFromUpdate.getState().equals(userStateRepo.findByUserState(INPUT_AMOUNT))) {
                                userService.calculateCourse(update);
                            }

                    }
                }
            }




        }


        else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            User userFromUpdate = userService.getUserFromUpdate(update);
            if (data.equals("XABAR")) {
                adminService.sendMessageToAdmin(update);
            }
            else if (data.equals("INFORMATION")){
                userService.sendInformationToUser(update);
            }
            else if (data.equals("CONVERTOR")){
                userService.convertor(update);
            }
            else if (data.equals("LIST")){
                adminService.getUserList(update);
            }
            else if (userFromUpdate.getState().equals(userStateRepo.findByUserState(GET_INFORMATION))){
                userService.getInformation(update);
            }
            else if (userFromUpdate.getState().equals(userStateRepo.findByUserState(ENTERED_WRONG_PASSWORD_FOR_ADMIN))){
                adminService.reenterPasswordOrMainMenu(update);
            }
            else if (userFromUpdate.getState().equals(userStateRepo.findByUserState(CONVERTOR))){
                userService.getInformationAboutConvertor(update);
            }
        }
    }
}
