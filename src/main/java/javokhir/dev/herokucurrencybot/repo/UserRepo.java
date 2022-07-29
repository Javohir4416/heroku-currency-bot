package javokhir.dev.herokucurrencybot.repo;

import javokhir.dev.herokucurrencybot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
