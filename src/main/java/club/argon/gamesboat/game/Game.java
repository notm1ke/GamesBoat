package club.argon.gamesboat.game;

import club.argon.gamesboat.Base;
import club.argon.gamesboat.game.event.GameMessageEvent;
import club.argon.gamesboat.game.lobby.GameLobby;
import club.argon.gamesboat.game.messaging.GameMessage;
import club.argon.gamesboat.game.messaging.GameMessageType;
import club.argon.gamesboat.util.DiscordUtils;
import club.argon.gamesboat.util.Embeds;

import co.m1ke.basic.callback.Callback;
import co.m1ke.basic.events.listener.Listener;
import co.m1ke.basic.utils.JsonSerializable;
import co.m1ke.basic.utils.container.Key;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.hooks.EventListener;

public abstract class Game extends Listener implements JsonSerializable, EventListener {

    private String name;
    private String shortened;
    private GameMessage messages;
    private GameState state;
    private ArrayList<User> users;
    private ArrayList<PrivateChannel> channels;
    private int minPlayers;
    private int maxPlayers;

    private GameManager manager;
    private GameLobby lobby;

    public Game(String name, String shortened, GameMessage messages, int minPlayers, int maxPlayers) {
        super(name + " (" + UUID.randomUUID() + ")", Base.getEventManager());

        this.name = name;
        this.shortened = shortened;
        this.messages = messages;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.users = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.state = GameState.WAITING;
        this.manager = null;
        this.lobby = null;
    }

    public abstract void start();
    public abstract void stop();

    public abstract void onEmit(GameMessageEvent event);

    public abstract void onJoin(User user);
    public abstract void onQuit(User user);

    public void emit(String msg) {
        emit(new MessageBuilder(msg).build());
    }

    public void emit(Message msg) {
        this.channels.forEach(pc -> pc.sendMessage(msg).queue());
    }

    public void emit(Message msg, Callback callback) {
        emit(msg);
        callback.complete();
    }

    public void emit(Message msg, Consumer<Message> callback) {
        this.channels.forEach(pc -> pc.sendMessage(msg).queue(callback));
    }

    public ArrayList<Message> emitWithResult(Message msg) {
        ArrayList<Message> messages = new ArrayList<>();
        for (PrivateChannel channel : this.channels) {
            channel.sendMessage(msg).queue(messages::add);
        }

        return messages;
    }

    public ArrayList<Message> emitWithResult(Message msg, Consumer<Message> callback) {
        ArrayList<Message> messages = new ArrayList<>();
        for (PrivateChannel channel : this.channels) {
            channel.sendMessage(msg).queue(callback.andThen(messages::add));
        }

        return messages;
    }

    public void emit(GameMessageType type, Key... formatting) {
        String s = String.format(GameMessageType.getType(this.messages, type), (Object[]) formatting);
        emit(Embeds.of(name, Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[] { s }));
    }

    public String getName() {
        return name;
    }

    public String getShortened() {
        return shortened;
    }

    public GameMessage getMessages() {
        return messages;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void addPlayer(User user) {
        this.users.add(user);
        this.channels.add(DiscordUtils.getPrivateChannel(user));
    }

    public void removePlayer(User user) {
        PrivateChannel channel = DiscordUtils.getPrivateChannel(user);
        channel.close().queue();

        this.channels.remove(channel);
    }

    public void prunePlayers() {
        this.users.forEach(this::removePlayer);
        this.users.clear();
    }

    public void pruneLobby() {
        this.getGameManager().destroyLobby(this, this.getLobby().getSerial());
    }

    public ArrayList<PrivateChannel> getChannels() {
        return channels;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public boolean isFull() {
        return users.size() >= getMaxPlayers();
    }

    public boolean canStart() {
        return isFull() || getUsers().size() >= getMinPlayers();
    }

    public GameManager getGameManager() {
        return this.manager;
    }

    public void setGameManager(GameManager manager) {
        this.manager = manager;
    }

    public GameLobby getLobby() {
        return lobby;
    }

    public void setLobby(GameLobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject()
                .put("name", this.name)
                .put("shortened", this.shortened)
                .put("state", this.state.name())
                .put("users", new JSONArray(users))
                .put("maxPlayers", this.maxPlayers)
                .put("minPlayers", this.minPlayers);
    }

}
