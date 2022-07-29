package javokhir.dev.herokucurrencybot.entity;

import javokhir.dev.herokucurrencybot.payload.enums.UserStateNames;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserState {
    @Id
    @GeneratedValue
    private Long state_id;
    @Enumerated(EnumType.STRING)
    private UserStateNames userState;
    public UserState(UserStateNames userState) {
        this.userState = userState;
    }
}
