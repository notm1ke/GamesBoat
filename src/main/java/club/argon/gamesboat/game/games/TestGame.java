package club.argon.gamesboat.game.games;

import club.argon.gamesboat.game.Game;
import club.argon.gamesboat.game.GameManager;
import club.argon.gamesboat.game.event.GameMessageEvent;
import club.argon.gamesboat.game.messaging.GameMessage;
import club.argon.gamesboat.game.messaging.GameMessageType;
import club.argon.gamesboat.util.DiscordUtils;

import co.m1ke.basic.utils.container.Key;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

/**
 * This game will be removed before the final submission.
 * It is purely a test of the event system and game systems as a whole.
 */
public class TestGame extends Game {

    public TestGame(GameManager manager) {
        super("Test", "TS",
                new GameMessage("*This game is a test*\nSay something to your teammate!",
                        "Thanks for playing this test game!", "An error occurred, so the game was ended.", "There are not enough players to continue this game.",
                        "An error occurred that prevented the game from continuing.", GameMessage.GENERIC_PLAYER_JOIN, GameMessage.GENERIC_PLAYER_QUIT),
                2, 2);
        this.setGameManager(manager);
    }

    @Override
    public void start() {
        this.emit(GameMessageType.GAME_START);
    }

    @Override
    public void init() {
    }

    @Override
    public void stop() {
        this.emit(GameMessageType.GAME_END);
        this.prunePlayers();
        this.pruneLobby();
    }

    @Override
    public void onJoin(User user) {
        this.emit(GameMessageType.PLAYER_JOIN, Key.of(user.getName()));
    }

    @Override
    public void onQuit(User user) {
        this.emit(GameMessageType.PLAYER_QUIT, Key.of(user.getName()));

        if (this.getUsers().size() < this.getMinPlayers()) {
            this.stop();
        }
    }

    @Override
    public void onEvent(Event event) {

    }

    @Override
    public void onEmit(GameMessageEvent event) {
        String content = event.getMessage();
        this.emit(DiscordUtils.bold(event.getUser().getName()) + " said " + DiscordUtils.code(content));
    }

}
