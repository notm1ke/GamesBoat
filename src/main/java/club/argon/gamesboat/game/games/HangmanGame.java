package club.argon.gamesboat.game.games;

import club.argon.gamesboat.Base;
import club.argon.gamesboat.game.Game;
import club.argon.gamesboat.game.event.GameMessageEvent;
import club.argon.gamesboat.game.messaging.GameMessage;
import club.argon.gamesboat.game.messaging.GameMessageType;
import club.argon.gamesboat.util.DiscordUtils;

import org.json.JSONArray;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

public class HangmanGame extends Game {

    private String word;
    private JSONArray words;

    public HangmanGame() {
        super("Hangman", "HM",
                new GameMessage("Hang that man (or don't)\nWhen it becomes your turn, guess a letter in the puzzle word.",
                        DiscordUtils.code("%s") + " won the game.", GameMessage.GENERIC_GAME_END_ERROR, GameMessage.GENERIC_GAME_END_PLAYERS,
                        GameMessage.GENERIC_ERROR_INTERRUPT, GameMessage.GENERIC_PLAYER_JOIN, GameMessage.GENERIC_PLAYER_QUIT),
                1, 2);
        this.words = Base.getPreferences().getWordList();
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

    }

    @Override
    public void onJoin(User user) {

    }

    @Override
    public void onQuit(User user) {

    }

    @Override
    public void onEvent(Event event) {

    }

    @Override
    public void onEmit(GameMessageEvent event) {

    }

}
