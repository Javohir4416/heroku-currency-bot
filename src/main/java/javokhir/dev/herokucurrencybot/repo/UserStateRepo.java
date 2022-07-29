package javokhir.dev.herokucurrencybot.repo;


import javokhir.dev.herokucurrencybot.entity.UserState;
import javokhir.dev.herokucurrencybot.payload.enums.UserStateNames;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStateRepo extends JpaRepository<UserState,Long> {
    UserState findByUserState(UserStateNames userState);
}
