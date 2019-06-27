package club.argon.gamesboat.game;

import club.argon.gamesboat.commands.GameCommand;
import club.argon.gamesboat.game.games.ConnectFourGame;
import club.argon.gamesboat.game.games.RockPaperScissors;
import club.argon.gamesboat.game.games.TestGame;
import club.argon.gamesboat.game.lobby.GameLobby;
import club.argon.gamesboat.game.lobby.GameLobbyType;

import co.m1ke.basic.logger.Logger;
import co.m1ke.basic.utils.Lang;
import co.m1ke.basic.utils.timings.Timings;

import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

public class GameManager {

    private ArrayList<Game> games;
    private ArrayList<GameLobby> lobbies;

    private Logger logger;
    private JDA discord;
    private CommandClient commandClient;
    private EventWaiter eventWaiter;

    public GameManager(JDA discord, CommandClient commandClient, EventWaiter eventWaiter) {
        Timings timings = new Timings("Games", "Initialization");
        this.logger = new Logger("Games");
        this.discord = discord;
        this.commandClient = commandClient;
        this.eventWaiter = eventWaiter;

        this.games = new ArrayList<Game>() {
            {
                add(new TestGame(GameManager.this));
                add(new ConnectFourGame(GameManager.this, eventWaiter));
                add(new RockPaperScissors(GameManager.this));
            }
        };
        this.lobbies = new ArrayList<>();

        commandClient.addCommand(new GameCommand(this));
        discord.addEventListener(new GameListener(this));

        timings.complete("Games Manager is now ready in %c%tms" + Lang.RESET + ".");
    }

    public void createLobby(User user, Game game, GameLobbyType type) {
        GameLobby lobby = new GameLobby(lobbies.size() + 1, user, game, type);
        lobbies.add(lobby);
        logger.info("Game Lobby #" + lobby.getSerial() + " for " + lobby.getGame().getName() + " created.");
    }

    public void destroyLobby(Game game, long serial) {
        GameLobby lobby = getLobby(game, serial);
        if (lobby == null) {
            return;
        }
        lobbies.remove(lobby);
        logger.info("Game Lobby #" + lobby.getSerial() + " has been destroyed.");
    }

    public void destroyLobby(GameLobby lobby) {
        destroyLobby(lobby.getGame(), lobby.getSerial());
    }

    public void joinLobby(User user, GameLobby lobby) {
        lobby.addUser(user);
    }

    public Game getGame(String input) {
        for (Game gb : games) {
            if (gb.getName().equalsIgnoreCase(input)
                    || gb.getShortened().equalsIgnoreCase(input)) {
                return gb;
            }
        }
        return null;
    }

    public GameLobby searchForLobby(Game Game) {
        return this.searchForLobby(Game.getName());
    }

    public GameLobby searchForLobby(String gameName) {
        for (GameLobby lobby : lobbies) {
            Game Game = lobby.getGame();
            if (Game.getName().equalsIgnoreCase(gameName)
                    || Game.getShortened().equalsIgnoreCase(gameName)
                    && Game.getUsers().size() < Game.getMaxPlayers()) {
                return lobby;
            }
        }
        return null;
    }

    public GameLobby searchForUser(User sender) {
        for (GameLobby lobby : lobbies) {
            if (lobby.getGame().getUsers().contains(sender)) {
                return lobby;
            }
        }
        return null;
    }

    public boolean hasLobby(User user) {
        for (GameLobby lobby : lobbies) {
            if (lobby.getGame().getUsers().contains(user)) {
                return true;
            }
        }
        return false;
    }

    public GameLobby getLobby(Game game, long serial) {
        for (GameLobby lobby : lobbies) {
            if (lobby.getGame().equals(game)
                    && lobby.getSerial() == serial) {
                return lobby;
            }
        }
        return null;
    }

    public ImmutableList<Game> getGames() {
        return ImmutableList.copyOf(games);
    }

    public ImmutableList<GameLobby> getLobbies() {
        return ImmutableList.copyOf(lobbies);
    }
}