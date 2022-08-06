package javokhir.dev.herokucurrencybot.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javokhir.dev.herokucurrencybot.entity.User;
import javokhir.dev.herokucurrencybot.feign.TelegramFeign;
import javokhir.dev.herokucurrencybot.payload.Currency;
import javokhir.dev.herokucurrencybot.repo.UserRepo;
import javokhir.dev.herokucurrencybot.repo.UserStateRepo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import static javokhir.dev.herokucurrencybot.payload.enums.UserStateNames.*;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.name;

@Service
@RequiredArgsConstructor
public class UserService {
      String name=null;
      String rate=null;
      double amount=0.0;


    private final UserStateRepo userStateRepo;


    private final UserRepo userRepo;


    private final ReplyMarkup replyMarkup;

    private final TelegramFeign telegramFeign;
    public User getUserFromUpdate(Update update){
        if (update.hasMessage()){
            org.telegram.telegrambots.meta.api.objects.User userFromUpdate = update.getMessage().getFrom();
            Long id = userFromUpdate.getId();
            Optional<User> optionalUser = userRepo.findById(id);
            User user;
            user = optionalUser.orElseGet(() -> new User(userFromUpdate.getId(),
                    userFromUpdate.getFirstName(),
                    userFromUpdate.getLastName(),
                    userFromUpdate.getUserName()));
            user=userRepo.save(user);
            return user;
        }
        else {
            Long id = update.getCallbackQuery().getFrom().getId();
            Optional<User> optionalUser = userRepo.findById(id);
            User user=new User();
            if (optionalUser.isPresent()) {
                user=optionalUser.get();
            }
            return user;
        }
    }

    public void sendInformationToUser(Update update) {
        User userFromUpdate = getUserFromUpdate(update);
        userFromUpdate.setState(userStateRepo.findByUserState(GET_INFORMATION));
        userRepo.save(userFromUpdate);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userFromUpdate.getId().toString());
        sendMessage.setText("Bu yerda qulaylik uchun mashxur valyutalar berilgan . Birini tanlashingiz yoki qolgan valyutalar ro'yxati  " +
                "tugmasi orqali boshqa valyutalar haqida ma'lumot olishingiz mumkin  ");
        sendMessage.setReplyMarkup(replyMarkup.inlineMarkup(userFromUpdate));
        telegramFeign.sendMessageToUser(sendMessage);
    }

    @SneakyThrows
    public void getInformation(Update update) {
        User userFromUpdate = getUserFromUpdate(update);
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(userFromUpdate.getId().toString());
        String data = update.getCallbackQuery().getData();
        URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
        URLConnection urlConnection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Currency[] currencies = gson.fromJson(reader, Currency[].class);
                if (data.equals("OTHERS")){
                    StringBuilder list= new StringBuilder();
                    for (Currency currency : currencies) {
                        list.append("1 ").append(currency.getCcy()).append(" ( ").append(currency.getCcyNmUZ()).
                                append(" ) ").append("\n").append("      ||      \n").append(currency.getRate()).append(" UZS (so'm)\n");
                    }
                    sendMessage.setText(list.toString());
                }
                else if (data.equals("BACK_TO_MENU")) {
                    showMenu(update);
                }
                else {
                    for (Currency currency : currencies) {
                        if (data.equals(currency.getCcy())) {
                            sendMessage.setText("1 " + currency.getCcy() + " ( " + currency.getCcyNmUZ() + " )\n" + "      ||      \n" + currency.getRate() + " UZS(so'm)");
                        }
                    }
                }
        telegramFeign.sendMessageToUser(sendMessage);
    }


    public void showMenu(Update update) {
        User user = getUserFromUpdate(update);
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(user.getId().toString());
        sendMessage.setText("Xizmatlardan birini tanlang : ");
        user.setState(userStateRepo.findByUserState(SHOW_MENU));
        user=userRepo.save(user);
        sendMessage.setReplyMarkup(replyMarkup.inlineMarkup(user));
        telegramFeign.sendMessageToUser(sendMessage);
    }

    public void convertorToUzbek(Update update) {
        User user = getUserFromUpdate(update);
        user.setState(userStateRepo.findByUserState(CONVERTOR_TO_UZBEK));
        userRepo.save(user);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId().toString());
        sendMessage.setText("Bu bo'limda siz boshqa valyutalarni o'zbek so'miga konvertatsiya qilishiz mumkin . Birini tanlang yoki o'zingiz xohlagan valyutaning qisqartmasini " +
                "yozing ( masalan RUB , qolgan valyuta qisqartmalarini qolgan valyutalar ro'yxati  tugmasi orqali olishingiz mumkin   ) ");
        sendMessage.setReplyMarkup(replyMarkup.inlineMarkup(user));
        telegramFeign.sendMessageToUser(sendMessage);
    }

    public void getInformationAboutConvertor(Update update) throws IOException {
        User user = getUserFromUpdate(update);
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(user.getId().toString());
        String data = update.getCallbackQuery().getData();
        URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
        URLConnection urlConnection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Currency[] currencies = gson.fromJson(reader, Currency[].class);
        if (data.equals("OTHERS")){
            StringBuilder list= new StringBuilder();
            for (Currency currency : currencies) {
                list.append(currency.getCcyNmUZ()).append("  ➡️").append(currency.getCcy()).append("\n");
            }
            sendMessage.setText(list.toString());
        }
        else if (data.equals("BACK_TO_CONVERTOR")) {
            chooseConvertor(update);
        }
        else {
            for (Currency currency : currencies) {
                if (data.equals(currency.getCcy())) {
                    sendMessage.setText("Siz "+ currency.getCcy() +" ( "+ currency.getCcyNmUZ()+ " ) " +"ni tanladingiz . Miqdorni kiriting (Masalan ,100)");
                    name = currency.getCcy()+" ( "+currency.getCcyNmUZ() +" ) ";
                    rate = currency.getRate();
                }
            }
            user.setState(userStateRepo.findByUserState(INPUT_AMOUNT_FOR_UZBEK));
            userRepo.save(user);
        }
        telegramFeign.sendMessageToUser(sendMessage);
    }
    @SneakyThrows
    public void getCurrencyForConvertToUzbek(Update update) {
        User user = getUserFromUpdate(update);
        String inputText = update.getMessage().getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId().toString());
        URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
        URLConnection urlConnection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Currency[] currencies = gson.fromJson(reader, Currency[].class);
        boolean has = false;
        for (Currency currency : currencies) {
            if (inputText.equals(currency.getCcy())) {
                has = true;
                name = currency.getCcy()+" ( "+currency.getCcyNmUZ() +" ) ";
                rate = currency.getRate();
                sendMessage.setText("Siz " + currency.getCcyNmUZ() + " ni tanladingiz . Miqdorni kiriting (Masalan ,100)");
                user.setState(userStateRepo.findByUserState(INPUT_AMOUNT_FOR_UZBEK));
                user=userRepo.save(user);
                telegramFeign.sendMessageToUser(sendMessage);
            }
        }
        if (!has) {
            sendMessage.setText("Bunday valyuta yo'q.Valyuta qisqartmasini to'g'ri va aniq kiriting!!!");
            telegramFeign.sendMessageToUser(sendMessage);
        }
    }

    public void calculateCourseToUzbek(Update update) {
        User user = getUserFromUpdate(update);
        String inputText = update.getMessage().getText();
        int counter=0; boolean state=false;
        for (int i=0; i<inputText.length(); i++) {
            char a=inputText.charAt(i);
            if (Character.isDigit(a)|| a=='.'){
                state=false;
                if (a=='.') {counter++;}
            } else {
                state=true; break;
            }
        }
        if (state || counter>1){
            SendMessage sendMessage=new SendMessage();
            sendMessage.setChatId(user.getId().toString());
            sendMessage.setText("Majvud raqam kiritilmadi.Qaytadan kiriting!!!");
            telegramFeign.sendMessageToUser(sendMessage);
        }
        else {
            double course = Double.parseDouble(rate);
            amount=Double.parseDouble(inputText);
            float v= (float) (amount * course);
            SendMessage sendMessage=new SendMessage();
            sendMessage.setChatId(user.getId().toString());
            sendMessage.setText(amount +"   "+ name  + "\n"+"      ||      \n"+ v  + " UZS (so'm)");
            user.setState(userStateRepo.findByUserState(CHOOSE_CONVERTOR));
            userRepo.save(user);
//            sendMessage.setReplyMarkup(replyMarkup.inlineMarkup(user));
            telegramFeign.sendMessageToUser(sendMessage);
        }
    }

    public void stats(Update update) {
        User user = getUserFromUpdate(update);
        List<User> userList = userRepo.findAll();
        SendMessage sendMessage =new SendMessage();
        sendMessage.setChatId(user.getId().toString());
        sendMessage.setText("Bot foydalanuvchilari soni : "+ userList.size());
        telegramFeign.sendMessageToUser(sendMessage);
    }

    public void chooseConvertor(Update update) {
        User user = getUserFromUpdate(update);
        user.setState(userStateRepo.findByUserState(CHOOSE_CONVERTOR));
        user=userRepo.save(user);
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(user.getId().toString());
        sendMessage.setText("Qaysi xizmatni amalga oshirmoqchisiz ? ");
        sendMessage.setReplyMarkup(replyMarkup.inlineMarkup(user));
        telegramFeign.sendMessageToUser(sendMessage);
    }

    public void convertorFromUzbek(Update update) {

        User user = getUserFromUpdate(update);
        user.setState(userStateRepo.findByUserState(CONVERTOR_FROM_UZBEK));
        user=userRepo.save(user);
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(user.getId().toString());
        sendMessage.setText("Bu bo'limda siz o'zbek so'midan jahon valyutalariga konvertatsiya qilishingiz mumkin . " +
                "Valyuta qisqartmasini kiriting ");

        sendMessage.setReplyMarkup(replyMarkup.inlineMarkup(user));
        telegramFeign.sendMessageToUser(sendMessage);
    }

    @SneakyThrows
    public void getCurrencyForConvertFromUzbek(Update update) {
            User user = getUserFromUpdate(update);
            String inputText = update.getMessage().getText();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(user.getId().toString());
            URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
            URLConnection urlConnection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Currency[] currencies = gson.fromJson(reader, Currency[].class);
            boolean has = false;
            for (Currency currency : currencies) {
                if (inputText.equals(currency.getCcy())) {
                    has = true;
                    name = currency.getCcy()+" ( "+currency.getCcyNmUZ() +" ) ";
                    rate = currency.getRate();
                    sendMessage.setText("Siz " + currency.getCcyNmUZ() + " ni tanladingiz . Hozir kirirtadigan pul miqdoringiz o'zbek" +
                            "so'midan "+ " "+ currency.getCcyNmUZ() + " ga konvertatsiya bo'ladi ");
                    user.setState(userStateRepo.findByUserState(INPUT_AMOUNT_FOR_CURRENCY));
                    user=userRepo.save(user);
                    telegramFeign.sendMessageToUser(sendMessage);
                }
            }
            if (!has) {
                sendMessage.setText("Bunday valyuta yo'q.Valyuta qisqartmasini to'g'ri va aniq kiriting!!!");
                telegramFeign.sendMessageToUser(sendMessage);
            }
        }

    public void calculateCourseFromUzbek(Update update) {
        User user = getUserFromUpdate(update);
        String inputText = update.getMessage().getText();
        int counter=0; boolean state1=false;
        for (int i=0; i<inputText.length(); i++) {
            char a=inputText.charAt(i);
            if (Character.isDigit(a)|| a=='.'){
                state1=false;
                if (a=='.') {counter++;}
            } else {
                state1=true; break;
            }
        }
        if (state1 || counter>1){
            SendMessage sendMessage=new SendMessage();
            sendMessage.setChatId(user.getId().toString());
            sendMessage.setText("Majvud raqam kiritilmadi.Qaytadan kiriting!!!");
            telegramFeign.sendMessageToUser(sendMessage);
        }
        else {
            double course = Double.parseDouble(rate);
            amount=Double.parseDouble(inputText);
            float v= (float) (amount/course);
            SendMessage sendMessage=new SendMessage();
            sendMessage.setChatId(user.getId().toString());
            sendMessage.setText(amount + " UZS (so'm)\n" +  "      ||      \n"+ v + "  " +  name);
            user.setState(userStateRepo.findByUserState(CHOOSE_CONVERTOR));
            userRepo.save(user);
//            sendMessage.setReplyMarkup(replyMarkup.inlineMarkup(user));
            telegramFeign.sendMessageToUser(sendMessage);
        }
    }

    @SneakyThrows
    public void getListCurrencies(Update update) {
        URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
        URLConnection urlConnection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Currency[] currencies = gson.fromJson(reader, Currency[].class);
        User user = getUserFromUpdate(update);
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(user.getId().toString());
        StringBuilder list= new StringBuilder();
        for (Currency currency : currencies) {
            list.append(currency.getCcyNmUZ()).append("  ➡️").append(currency.getCcy()).append("\n");
        }
        sendMessage.setText(list.toString());
        telegramFeign.sendMessageToUser(sendMessage);
    }
}
