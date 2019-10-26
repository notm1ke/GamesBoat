package club.argon.gamesboat.game.games;

import club.argon.gamesboat.Base;
import club.argon.gamesboat.game.Game;
import club.argon.gamesboat.game.GameManager;
import club.argon.gamesboat.game.event.GameMessageEvent;
import club.argon.gamesboat.game.event.RoundEndEvent;
import club.argon.gamesboat.game.messaging.GameMessage;
import club.argon.gamesboat.game.messaging.GameMessageType;
import club.argon.gamesboat.util.DiscordUtils;
import club.argon.gamesboat.util.Embeds;

import co.m1ke.basic.events.interfaces.Event;
import co.m1ke.basic.utils.container.Key;
import co.m1ke.basic.utils.container.pair.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

public class RockPaperScissors extends Game {

    private enum GameCharacter {

        ROCK(new ArrayList<GameCharacter>() {
            {
                add(SCISSORS);
            }
        }),

        PAPER(new ArrayList<GameCharacter>() {
            {
                add(ROCK);
            }
        }),

        SCISSORS(new ArrayList<GameCharacter>() {
            {
                add(PAPER);
            }
        });

        private List<GameCharacter> canKill;

        GameCharacter(List<GameCharacter> canKill) {
            this.canKill = canKill;
        }

        public static boolean kill(GameCharacter you, GameCharacter com) {
            return you.canKill.contains(com);
        }

        public static boolean draw(GameCharacter you, GameCharacter com) {
            return you == com;
        }

        public static GameCharacter match(String in) {
            for (GameCharacter c : values()) {
                if (c.name().equalsIgnoreCase(in)) {
                    return c;
                }
            }
            return null;
        }

        public List<GameCharacter> getCanKill() {
            return canKill;
        }

        public void setCanKill(List<GameCharacter> canKill) {
            this.canKill = canKill;
        }

    }

    // The round number
    private int round;

    // The users
    private User user1;
    private User user2;

    // K = represents user #1, V = represents user #2
    private Pair<GameCharacter, GameCharacter> players;
    private Pair<Integer, Integer> stats;

    public RockPaperScissors(GameManager manager) {
        super("Rock Papers Scissors", "RPS",
                new GameMessage("Rock... papers.. scissors!\nType " + DiscordUtils.code("Rock") + ", " + DiscordUtils.code("Paper") + ", or " + DiscordUtils.code("Scissors") + " and wait until your opponent locks in their answer.",
                        DiscordUtils.code("%s") + " won the game.", GameMessage.GENERIC_GAME_END_ERROR, GameMessage.GENERIC_GAME_END_PLAYERS,
                        GameMessage.GENERIC_ERROR_INTERRUPT, GameMessage.GENERIC_PLAYER_JOIN, GameMessage.GENERIC_PLAYER_QUIT),
                2, 2);
        this.round = 1;
        this.setGameManager(manager);
    }

    @Override
    public void start() {
        this.emit(GameMessageType.GAME_START);

        this.user1 = this.getUsers().get(0);
        this.user2 = this.getUsers().get(1);

        this.players = Pair.construct(null, null);
        this.stats = Pair.construct(0, 0);
    }

    @Override
    public void init() {
    }

    @Override
    public void stop() {
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

        // A player leaving this game should always result in a game end (since there are only 2 maximum players).
        this.stop();
    }

    @Event
    public void onRoundEnd(RoundEndEvent event) {
        if (event.getRound() > event.getMax()) {
            User winner = (stats.getK() > stats.getV()) ? user1 : user2;
            if (winner == null) {
                this.emit(GameMessageType.GAME_END, Key.of("Nobody"));
                this.stop();
                return;
            }

            // Reward user
            this.emit(GameMessageType.GAME_END, Key.of(winner.getName()));
            this.stop();
            return;
        }

        players.setK(null);
        players.setV(null);

        this.emit(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                DiscordUtils.bold("Round #" + round) + " has commenced."
        }));
    }

    @Override
    public void onEvent(net.dv8tion.jda.core.events.Event event) {
    }

    @Override
    public void onEmit(GameMessageEvent event) {
        if (round > 3) {
            User winner = (stats.getK() > stats.getV()) ? user1 : user2;
            // Reward user
            this.emit(GameMessageType.GAME_END, Key.of(winner.getName()));
            this.stop();
            return;
        }

        String content = event.getMessage();
        GameCharacter character = GameCharacter.match(content);
        if (character == null) {
            event.getEvent().getChannel().sendMessage(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                    DiscordUtils.code(content) + " is not a valid choice."
            }, new LinkedList<MessageEmbed.Field>() {
                {
                    new MessageEmbed.Field("Valid Choices", Joiner.on(", ").join(GameCharacter.values()), true);
                }
            })).queue();
            return;
        }

        if (user1 == null && user2 == null) {
            event.getEvent().getChannel().sendMessage("The game is not ready.").queue();
            return;
        }

        // Match event player with user1 or user2, then set their option.
        if (event.getUser().getId().equalsIgnoreCase(user1.getId())) {
            if (players.getK() != null) {
                event.getEvent().getChannel().sendMessage(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                        "You have already locked in your choice."
                })).queue();
                return;
            }
            players.setK(character);
            event.getEvent().getChannel().sendMessage(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                    "You have selected " + DiscordUtils.code(character.name()) + " as your play.",
                    "This selection cannot be changed until the next round."
            })).queue();
            this.emit(DiscordUtils.bold(user1.getName()) + " is now ready.");
        } else if (event.getUser().getId().equalsIgnoreCase(user2.getId())) {
            if (players.getV() != null) {
                event.getEvent().getChannel().sendMessage(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                        "You have already locked in your choice."
                })).queue();
                return;
            }
            players.setV(character);
            event.getEvent().getChannel().sendMessage(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                    "You have selected " + DiscordUtils.code(character.name()) + " as your play.",
                    "This selection cannot be changed until the next round."
            })).queue();
            this.emit(DiscordUtils.bold(user2.getName()) + " is now ready.");
        }

        if (players.getK() != null && players.getV() != null) {
            // Both players have chosen their characters, proceeding..
            if (GameCharacter.draw(players.getK(), players.getV())) {
                this.emit(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                        "This round was a draw. " + DiscordUtils.bold("(" + stats.getK() + "-" + stats.getV() + ")")
                }));
            } else if (GameCharacter.kill(players.getK(), players.getV())) {
                stats.setK(stats.getK() + 1);
                this.emit(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                        DiscordUtils.code(user1.getName()) + " has won this round. " + DiscordUtils.bold("(" + stats.getK() + "-" + stats.getV() + ")")
                }));
            } else if (GameCharacter.kill(players.getV(), players.getK())) {
                stats.setV(stats.getV() + 1);
                this.emit(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                        DiscordUtils.code(user2.getName()) + " has won this round. " + DiscordUtils.bold("(" + stats.getV() + "-" + stats.getK() + ")")
                }));
            }
            round += 1;
            Base.getEventManager().getEventExecutor().emit(new RoundEndEvent(this, round, 3));
        }
    }

}
