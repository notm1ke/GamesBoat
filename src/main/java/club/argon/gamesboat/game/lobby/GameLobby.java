package club.argon.gamesboat.game.lobby;

import club.argon.gamesboat.Base;
import club.argon.gamesboat.game.Game;
import club.argon.gamesboat.game.GameState;
import club.argon.gamesboat.game.event.GameJoinEvent;
import club.argon.gamesboat.game.event.GameQuitEvent;

import co.m1ke.basic.logger.Logger;
import co.m1ke.basic.utils.JsonSerializable;

import org.json.JSONObject;

import net.dv8tion.jda.core.entities.User;

public class GameLobby implements JsonSerializable {

    private Logger logger;

    private long serial;
    private Game game;
    private GameLobbyType lobbyType;
    private GameLobbyHandler lobbyHandler;
    private User creator;

    public GameLobby(long serial, User creator, Game game, GameLobbyType lobbyType) {
        this.logger = new Logger("Games");

        this.serial = serial;
        this.game = game;
        this.lobbyType = lobbyType;
        this.lobbyHandler = new GameLobbyHandler(this, Base.getScheduler(), 15);
        this.creator = creator;

        this.game.setLobby(this);
        Base.getEventManager().getEventExecutor().registerListener(lobbyHandler);
    }

    public void addUser(User user) {
        Game game = this.getGame();
        game.addPlayer(user);
        game.onJoin(user);

        Base.getEventManager().getEventExecutor().emit(new GameJoinEvent(game, user));
    }

    public void removeUser(User user) {
        Game game = this.getGame();
        game.removePlayer(user);
        game.onQuit(user);

        Base.getEventManager().getEventExecutor().emit(new GameQuitEvent(game, user));

        // if the lobby is empty, and the game is not running, kill it.
        if (getInLobby() == 0 && game.getState() != GameState.ACTIVE) {
            Base.getGameManager().destroyLobby(this);
        }
    }

    public long getSerial() {
        return serial;
    }

    public Game getGame() {
        return game;
    }

    public GameLobbyType getLobbyType() {
        return lobbyType;
    }

    public GameLobbyHandler getHandler() {
        return lobbyHandler;
    }

    public User getCreator() {
        return creator;
    }

    public int getInLobby() {
        return getGame().getUsers().size();
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("serial", this.serial)
                .put("game", this.game.toJson())
                .put("lobbyType", this.lobbyType.name())
                .put("creator", this.creator.getId())
                .put("users", this.getInLobby());
    }

}
