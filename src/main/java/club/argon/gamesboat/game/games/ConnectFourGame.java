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
import club.argon.gamesboat.util.NumbersToWords;
import club.argon.gamesboat.util.emote.selection.EmojiSelection;
import club.argon.gamesboat.util.emote.selection.emoji.EmojiSelector;

import co.m1ke.basic.callback.Callback;
import co.m1ke.basic.events.interfaces.Event;
import co.m1ke.basic.utils.container.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

@SuppressWarnings("Duplicates")
public class ConnectFourGame extends Game {

    private List<String> reactions;

    private class Board {
        private Piece[][] pieces = new Piece[6][7];

        public Message visualize() {
            StringBuilder sb = new StringBuilder();
            sb.append("\b");

            for (int r = 0; r < pieces.length; r++) {
                Stream<Piece> curRow = Stream.of(pieces[r]);
                curRow.forEach(p -> {
                    if (p == null || p.getPlayer() == null) {
                        sb.append(":white_circle:" + " ");
                    } else {
                        sb.append(p.getPlayer().toEmote() + " ");
                    }
                });
                sb.append("\n");
            }

            sb.append("\n");

            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor("Games", null, Embeds.Icons.GAMES.getUrl());
            eb.setDescription(sb.toString().trim());

            eb.setFooter("GamesBoat", null);
            eb.setColor(Embeds.ColorType.NORMAL.getColor());
            MessageEmbed embed = eb.build();
            MessageBuilder mb = new MessageBuilder();
            mb.setEmbed(embed);

            return mb.build();
        }

        public void set(int c, GamePlayer player, Callback success, Callback failure) {
            if (pieces[0][c] != null) {
                failure.complete();
                return;
            }
            for (int i = 5; i >= 0; i--) {
                if (pieces[i][c] != null) {
                    continue;
                }
                pieces[i][c] = new Piece(player);
                break;
            }
            success.complete();
        }

        public Piece[][] getPieces() {
            return pieces;
        }
    }

    private class Piece {
        private GamePlayer player;
        private GameColor color;

        public Piece(GamePlayer player) {
            this.player = player;
            this.color = player.getColor();
        }

        public GamePlayer getPlayer() {
            return player;
        }

        public GameColor getColor() {
            return color;
        }
    }

    private class GamePlayer {
        private User user;
        private GameColor color;

        public GamePlayer(User user, GameColor color) {
            this.user = user;
            this.color = color;
        }

        public User getUser() {
            return user;
        }

        public GameColor getColor() {
            return color;
        }

        public String toEmote() {
            if (color == GameColor.BLUE) {
                return ":large_blue_circle:";
            }
            return ":red_circle:";
        }
        
        public Piece getPiece() {
            return new Piece(this);
        }
    }

    private enum GameColor {
        BLUE, RED
    }

    private Board board;
    private Message visualizedBoard;
    private ArrayList<Message> currentBoards;

    // The round number
    private int round;

    // The users
    private GamePlayer user1;
    private GamePlayer user2;
    private GamePlayer currentTurn;

    private EventWaiter eventWaiter;

    public ConnectFourGame(GameManager manager, EventWaiter eventWaiter) {
        super("Connect Four", "C4",
                new GameMessage("Connect your pieces!\nClick on a reaction in order to place a tile.",
                        DiscordUtils.code("%s") + " won the game.", GameMessage.GENERIC_GAME_END_ERROR, GameMessage.GENERIC_GAME_END_PLAYERS,
                        GameMessage.GENERIC_ERROR_INTERRUPT, GameMessage.GENERIC_PLAYER_JOIN, GameMessage.GENERIC_PLAYER_QUIT),
                2, 2);
        this.round = 1;
        this.eventWaiter = eventWaiter;

        this.setGameManager(manager);
    }

    @Override
    public void start() {
        this.emit(GameMessageType.GAME_START);

        this.user1 = new GamePlayer(this.getUsers().get(0), GameColor.BLUE);
        this.user2 = new GamePlayer(this.getUsers().get(1), GameColor.RED);

        this.currentTurn = getRandom(new ArrayList<GamePlayer>() {
            {
                add(user1);
                add(user2);
            }
        });

        this.board = new Board();
        this.visualizedBoard = board.visualize();
        this.reactions = new ArrayList<String>() {
            {
                for (int i = 1; i < 8; i++) {
                    add(EmojiParser.parseToUnicode(":" + NumbersToWords.convert((long) i) + ":"));
                }
            }
        };

        for (PrivateChannel channel : getChannels()) {
            if (currentTurn.getUser().getId().equals(channel.getUser().getId())) {
                channel.sendMessage(Embeds.of("Games", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[]{
                        "It is now your turn! You are " + currentTurn.toEmote() + "."
                })).queue();
                continue;
            }
            channel.sendMessage(Embeds.of("Games", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[]{
                    "It is " + currentTurn.getUser().getAsMention() + "'s turn.",
            })).queue();
        }

        this.currentBoards = this.emitWithResult(visualizedBoard, (m) -> {
            if (m.getPrivateChannel().getUser().getId().equals(currentTurn.getUser().getId())) {
                EmojiSelector.addEmojiSelection(m.getPrivateChannel().getUser().getId(), new EmojiSelection(m, m.getPrivateChannel().getUser(), reactions) {
                    @Override
                    public void action(int chose) {
                        switch (chose) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                updateBoard(currentTurn, chose);
                                return;
                            default:
                                break;
                        }
                    }
                });
            }
        });
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
        if (hasFour(currentTurn)) {
            User winner = currentTurn.getUser();

            // Reward user
            this.emit(GameMessageType.GAME_END, Key.of(winner.getName()));
            this.stop();
            return;
        }

        if (isBoardFull() && !hasFour(user1) && !hasFour(user2)) {
            // board full and game concluded.
            this.emit(GameMessageType.GAME_END, Key.of("Nobody"));
            this.stop();
            return;
        }

        if (currentTurn.getUser().getId().equals(user1.getUser().getId())) {
            currentTurn = user2;
        } else {
            currentTurn = user1;
        }

        this.currentBoards = this.emitWithResult(visualizedBoard, (m) -> {
            if (currentTurn.getUser().getId().equals(m.getPrivateChannel().getUser().getId())) {
                EmojiSelector.addEmojiSelection(m.getPrivateChannel().getUser().getId(), new EmojiSelection(m, m.getPrivateChannel().getUser(), reactions) {
                    @Override
                    public void action(int chose) {
                        switch (chose) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                updateBoard(currentTurn, chose);
                                return;
                            default:
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onEvent(net.dv8tion.jda.core.events.Event event) {
    }

    @Override
    public void onEmit(GameMessageEvent event) {
    }

    private void updateBoard(GamePlayer player, int selection) {
        // r = up and down
        // c = side to side
        board.set(selection, player, () -> {

        }, () -> {

        });

        currentBoards.forEach(p -> p.delete().queue());
        currentBoards.clear();

        visualizedBoard = board.visualize();
        Base.getEventManager().getEventExecutor().emit(new RoundEndEvent(this, 0, 0));
    }

    private boolean hasFour(GamePlayer player) {
        Piece[][] pieces = board.getPieces();
        GameColor color = player.getColor();

        // Rows
        for (int r = 0; r < pieces.length; r++) {
            for (int c = 0; c < pieces[0].length; c++) {
                Piece piece1 = getPiece(r, c);
                Piece piece2 = getPiece(r, c+1);
                Piece piece3 = getPiece(r, c+2);
                Piece piece4 = getPiece(r, c+3);

                if (piece1 == null
                        || piece2 == null
                        || piece3 == null
                        || piece4 == null) {
                    continue;
                }

                // Checks if all pieces in the row are equal to player's color then return true
                if (piece1.getColor() == color
                        && piece2.getColor() == color
                        && piece3.getColor() == color
                        && piece4.getColor() == color) {
                    return true;
                }
            }
        }

        // Columns
        for (int c = 0; c < pieces.length; c++) {
            for (int r = 0; r < pieces[0].length; r++) {
                Piece piece1 = getPiece(r, c);
                Piece piece2 = getPiece(r+1, c);
                Piece piece3 = getPiece(r+2, c);
                Piece piece4 = getPiece(r+3, c);

                if (piece1 == null
                        || piece2 == null
                        || piece3 == null
                        || piece4 == null) {
                    continue;
                }

                // Checks if all pieces in the row are equal to player's color then return true
                if (piece1.getColor() == color
                        && piece2.getColor() == color
                        && piece3.getColor() == color
                        && piece4.getColor() == color) {
                    return true;
                }
            }
        }

        // Descending Diagonals
        for (int r = 0; r < pieces.length; r++) {
            for (int c = 0; c < pieces[0].length; c++) {
                Piece piece1 = getPiece(r, c);
                Piece piece2 = getPiece(r+1, c+1);
                Piece piece3 = getPiece(r+2, c+2);
                Piece piece4 = getPiece(r+3, c+3);

                if (piece1 == null
                        || piece2 == null
                        || piece3 == null
                        || piece4 == null) {
                    continue;
                }

                // Checks if all pieces in the row are equal to player's color then return true
                if (piece1.getColor() == color
                        && piece2.getColor() == color
                        && piece3.getColor() == color
                        && piece4.getColor() == color) {
                    return true;
                }
            }
        }

        // Ascending Diagonals
        for (int r = 0; r < pieces.length; r++) {
            for (int c = 0; c < pieces[0].length; c++) {
                Piece piece1 = getPiece(r, c);
                Piece piece2 = getPiece(r+1, c-1);
                Piece piece3 = getPiece(r+2, c-2);
                Piece piece4 = getPiece(r+3, c-3);

                if (piece1 == null
                        || piece2 == null
                        || piece3 == null
                        || piece4 == null) {
                    continue;
                }

                // Checks if all pieces in the row are equal to player's color then return true
                if (piece1.getColor() == color
                        && piece2.getColor() == color
                        && piece3.getColor() == color
                        && piece4.getColor() == color) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isBoardFull() {
        Piece[][] pieces = board.getPieces();

        for (Piece[] piece : pieces) {
            for (Piece piecz : piece) {
                if (piecz == null) {
                    return false;
                }
            }
        }

        return true;
    }

    public Piece getPiece(int row, int column) {
        Piece[][] pieces = board.getPieces();
        if (column >= pieces[0].length || column < 0) {
            return null;
        }
        if (row >= pieces.length || row < 0) {
            return null;
        }
        return pieces[row][column];
    }

    private <T> T getRandom(List<T> array) {
        return array.get(new Random().nextInt(array.size() - 1));
    }

}
