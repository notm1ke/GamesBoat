package club.argon.gamesboat.game.messaging;

import club.argon.gamesboat.util.DiscordUtils;

import lombok.Data;

@Data
public class GameMessage {

    private String gameStart;
    private String gameEnd;
    private String gameEndError;
    private String gameEndPlayers;
    private String errorInterrupt;

    private String playerJoin;
    private String playerQuit;

    public static final String GENERIC_PLAYER_JOIN = DiscordUtils.code("%s") + " joined.";
    public static final String GENERIC_PLAYER_QUIT = DiscordUtils.code("%s") + " quit.";
    public static final String GENERIC_GAME_END_ERROR = "An error occurred, so the game was ended.";
    public static final String GENERIC_GAME_END_PLAYERS = "There are not enough players to continue this game";
    public static final String GENERIC_ERROR_INTERRUPT = "An error occurred that prevented the game from continuing. You are encouraged to contact a developer regarding this incident.";

    public GameMessage(String gameStart, String gameEnd, String gameEndError, String gameEndPlayers, String errorInterrupt, String playerJoin, String playerQuit) {
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
        this.gameEndError = gameEndError;
        this.gameEndPlayers = gameEndPlayers;
        this.errorInterrupt = errorInterrupt;
        this.playerJoin = playerJoin;
        this.playerQuit = playerQuit;
    }

}
