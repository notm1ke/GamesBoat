package club.argon.gamesboat.game.lobby;

import club.argon.gamesboat.Base;
import club.argon.gamesboat.game.GameState;
import club.argon.gamesboat.game.event.GameJoinEvent;
import club.argon.gamesboat.scheduler.SimpleRunnableImpl;
import club.argon.gamesboat.util.DiscordUtils;
import club.argon.gamesboat.util.Embeds;

import co.m1ke.basic.events.interfaces.Event;
import co.m1ke.basic.events.listener.Listener;
import co.m1ke.basic.scheduler.SimpleScheduler;
import co.m1ke.basic.utils.TimeUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

@SuppressWarnings("Duplicates")
public class GameLobbyHandler extends Listener {

    private GameLobby lobby;
    private SimpleScheduler scheduler;

    private int countdown;
    private int countdownInitial;
    private boolean countdownStarted;

    public GameLobbyHandler(GameLobby lobby, SimpleScheduler scheduler, int countdownInitial) {
        super("Game Lobby", Base.getEventManager());

        this.lobby = lobby;
        this.scheduler = scheduler;

        this.countdown = countdownInitial;
        this.countdownInitial = countdown;
        this.countdownStarted = false;
    }

    @Override
    public void init() {
    }

    @Event
    public void onJoin(GameJoinEvent event) {
        // Verify that this is the lobby in the emitted event
        if (event.getGame().getLobby() != null && event.getGame().getLobby() == this.lobby) {

            // The game has already started, notify the user and remove them.
            if (event.getGame().getState() != GameState.WAITING) {
                DiscordUtils.getPrivateChannel(event.getUser()).sendMessage(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                        "I'm sorry, but **" + event.getGame().getName() + " #" + event.getGame().getLobby().getSerial() + "** has already started.",
                        "You may use " + DiscordUtils.code("!game join " + event.getGame().getShortened().toLowerCase()) + " to queue for another lobby.",
                })).queue();
                lobby.removeUser(event.getUser());
                return;
            }

            // If the game cannot yet be started, emit a message saying "x more people are needed"
            if (!countdownStarted && !event.getGame().canStart()) {
                event.getGame().emit(DiscordUtils.code(event.getGame().getUsers().size() + "/" + event.getGame().getMinPlayers()) + " are needed to start the game.");
                return;
            }

            // If the countdown is not running, and the game is now startable, start it!
            if (!countdownStarted && event.getGame().canStart()) {
                initiateCountdown();
                return;
            }

            // This will not affect the current countdown if it is already running.
            if (countdownStarted && event.getGame().canStart()) {
                return;
            }
        }
    }

    public void initiateCountdown() {
        if (!lobby.getGame().canStart()) {
            return;
        }

        ArrayList<Message> messages = lobby.getGame().emitWithResult(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[] {
                "The game is starting soon!"
        }, new LinkedList<MessageEmbed.Field>() {
            {
                add(new MessageEmbed.Field("Time Until Start",  countdownInitial + " second" + TimeUtil.numberEnding(countdownInitial), true));
                add(new MessageEmbed.Field("Players In Lobby", lobby.getGame().getUsers().size() + " player" + TimeUtil.numberEnding(lobby.getGame().getUsers().size()), true));
            }
        }));

        countdownStarted = true;
        this.scheduler.scheduleRepeatingTask(new SimpleRunnableImpl() {
            @Override
            public void run() {
                // Subtract countdownInitial
                countdown -= 5;

                // Countdown ended
                if (countdown <= 0) {

                    // Game unable to start
                    if (!lobby.getGame().canStart()) {
                        this.cancel();
                        return;
                    }

                    // Start game
                    flush(messages);
                    Base.getEventManager().getEventExecutor().registerListener(lobby.getGame());
                    lobby.getGame().start();
                    this.cancel();
                    return;
                }

                // Game unable to start
                if (!lobby.getGame().canStart()) {
                    int needed = lobby.getGame().getMinPlayers() - lobby.getGame().getUsers().size();
                    updateAll(messages, Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                            "The game could not be started because one or more members have left the lobby.",
                            DiscordUtils.code(String.valueOf(needed)) + " more member" + TimeUtil.numberEnding(needed) + " must join in order to start."
                    }));
                    this.cancel();
                    return;
                }

                // Update game starting messages
                tick(messages, countdown);
            }
        }, 0L, 5L, TimeUnit.SECONDS);
    }

    private void flush(ArrayList<Message> messages) {
        messages.forEach(m -> m.delete().queue());
        messages.clear();
    }

    private void tick(ArrayList<Message> messages, int countdown) {
        updateAll(messages, Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[] {
                "The game is starting soon!"
        }, new LinkedList<MessageEmbed.Field>() {
            {
                add(new MessageEmbed.Field("Time Until Start", countdown + " second" + TimeUtil.numberEnding(countdown), true));
                add(new MessageEmbed.Field("Players In Lobby", lobby.getGame().getUsers().size() + " player" + TimeUtil.numberEnding(lobby.getGame().getUsers().size()), true));
            }
        }));
    }

    private void updateAll(ArrayList<Message> messages, Message newMessage) {
        messages.forEach(m -> m.editMessage(newMessage).queue());
    }

}
