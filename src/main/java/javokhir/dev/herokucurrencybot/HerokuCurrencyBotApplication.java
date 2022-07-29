package javokhir.dev.herokucurrencybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HerokuCurrencyBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(HerokuCurrencyBotApplication.class, args);
    }

}
