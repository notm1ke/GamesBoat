package club.argon.gamesboat.player;

import lombok.Data;
import net.dv8tion.jda.core.entities.User;

@Data
public class Player {

    private User user;
    private int coins;

    public Player(User user, int coins) {
        this.user = user;
        this.coins = coins;

    }
}
