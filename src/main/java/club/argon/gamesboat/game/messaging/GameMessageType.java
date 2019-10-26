package club.argon.gamesboat.game.messaging;

import java.lang.reflect.Field;

public enum GameMessageType {

    GAME_START("gameStart"), GAME_END("gameEnd"), GAME_END_ERROR("gameEndError"), GAME_END_PLAYERS("gameEndPlayers"), ERROR_INTERRUPT("errorInterrupt"),
    PLAYER_JOIN("playerJoin"), PLAYER_QUIT("playerQuit");

    private String field;

    GameMessageType(String field) {
        this.field = field;
    }

    public static String getType(GameMessage message, GameMessageType type) {
        try {
            Field target = message.getClass().getDeclaredField(type.field);
            target.setAccessible(true);

            return (String) target.get(message);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return "An error occurred while fetching this message.";
        }
    }

}
