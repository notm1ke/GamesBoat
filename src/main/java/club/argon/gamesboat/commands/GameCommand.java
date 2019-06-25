package club.argon.gamesboat.commands;

import club.argon.gamesboat.game.Game;
import club.argon.gamesboat.game.GameManager;
import club.argon.gamesboat.game.lobby.GameLobby;
import club.argon.gamesboat.game.lobby.GameLobbyType;
import club.argon.gamesboat.util.DiscordUtils;
import club.argon.gamesboat.util.Embeds;

import co.m1ke.basic.utils.Lang;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Message;

public class GameCommand extends Command {

    private GameManager manager;

    public GameCommand(GameManager manager) {
        this.manager = manager;
        this.name = "game";
        this.help = "there is no help";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");

        GameLobby current = manager.searchForUser(event.getAuthor());
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("leave")) {
                if (current == null) {
                    event.reply(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                            "You are not currently in a game lobby."
                    }));
                    return;
                }
                current.removeUser(event.getAuthor());
                event.reply(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[] {
                        "You left " + DiscordUtils.bold(current.getGame().getShortened() + " #" + current.getSerial()) + "."
                }));
                return;
            }
            if (args[0].equalsIgnoreCase("list")) {
                StringBuilder sb = new StringBuilder();
                manager.getGames().forEach(g -> sb.append(g.getName() + ", "));
                String prettyList = Lang.stripLastChar(sb.toString().trim());

                event.reply(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[] {
                        "We currently have " + DiscordUtils.code(String.valueOf(manager.getGames().size())) + " playable games.",
                        DiscordUtils.code(prettyList),
                }));
                return;
            }
            event.reply(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[] {
                    current == null ? "You are not currently in a game lobby." : "You are currently in " + DiscordUtils.bold(current.getGame().getShortened() + " #" + current.getSerial()) + ".",
                    "",
                    "Welcome to GamesBoat! Here is a list of our game-related commands:",
                    DiscordUtils.bold("!game join <identifier>"),
                    DiscordUtils.bold("!game join <identifier> <lobby number>"),
                    DiscordUtils.bold("!game leave"),
                    DiscordUtils.bold("!game list")
                    // TODO: Add some more commands as features are implemented.
            }));
            return;
        }

        if (args.length > 4) {
            event.reply(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                    "You supplied too many arguments.",
                    "For a full help list, please use " + DiscordUtils.code("!game") + "."
            }));
            return;
        }

        if (args[0].equalsIgnoreCase("join")) {
            if (current != null) {
                event.reply(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                        "You are already in a game lobby.",
                        "To leave, use " + DiscordUtils.code("!game leave") + "."
                }));
                return;
            }

            String input = args[1];
            Game game = manager.getGame(input);

            if (input == null) {
                event.reply(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.RED, new String[] {
                        "Hmm, no game with name " + DiscordUtils.code(input) + " exists..",
                        "To view all of our games, use " + DiscordUtils.code("!game list") + ".",
                }));
                return;
            }

            GameLobby match = manager.searchForLobby(game);
            if (match == null) {
                Message msg = DiscordUtils.getPendingMessage(event, Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[] {
                        "Hmm, there aren't any lobbies for that game at the moment.",
                        "Please hold on, I will make a new lobby just for you.",
                }));

                manager.createLobby(event.getAuthor(), game, GameLobbyType.PUBLIC);
                GameLobby nm = manager.searchForLobby(game);
                nm.addUser(event.getAuthor());

                msg.editMessage(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[] {
                        "You have joined " + DiscordUtils.bold(nm.getGame().getName() + " #" + nm.getSerial()),
                })).queue();
                return;
            }

            match.addUser(event.getAuthor());
            event.reply(Embeds.of("Game", Embeds.Icons.GAMES, Embeds.ColorType.NORMAL, new String[] {
                    "You have joined " + DiscordUtils.bold(match.getGame().getName() + " #" + match.getSerial()),
            }));
        }

    }

}
