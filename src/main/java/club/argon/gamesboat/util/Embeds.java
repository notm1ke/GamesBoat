package club.argon.gamesboat.util;

import java.awt.Color;
import java.util.LinkedList;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

@SuppressWarnings("Duplicates")
public class Embeds {

    private static final String URL_BASE = "https://radium.pro/assets/icons/";

    public enum ColorType {

        NORMAL(DEFAULT_COLOR), RED(ERROR_COLOR), SUCCESS(SUCCESS_COLOR);

        private Color color;

        ColorType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

    }

    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    public enum Icons {

        ARGON("Argon", URL_BASE + "argon-logo.png"),
        ARGON_WHITE("Argon", URL_BASE + "argon-logo-white.png"),
        ARGON_PLATTER_RED("Argon", URL_BASE + "argon-red.png"),
        ARGON_PLATTER_WHITE("Argon", URL_BASE + "argon-white.png"),

        ERROR("Error", URL_BASE + "error.png"),
        STATUS("Status", URL_BASE + "status.png"),
        COMMANDS("Commands", URL_BASE + "commands.png"),
        ACCOUNT("Accounts", URL_BASE + "account.png"),
        COLORS("Colors", URL_BASE + "colors.png"),
        SERVERS("Servers", URL_BASE + "servers.png"),
        GAMES("Games", URL_BASE + "games.png"),
        MUSIC("Music", URL_BASE + "music.png"),
        QUOTE("Quote", URL_BASE + "quote.png"),
        TEXT("Text", URL_BASE + "text.png"),
        PUNISH("Punish", URL_BASE + "punishment.png"),
        XP("XP", URL_BASE + "xp.png");

        private String name;
        private String url;

        Icons(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    public enum Emotes {

        BOT_TAG("botTag", "230105988211015680"),
        ONLINE("online", "493263553395032064"),
        OFFLINE("offline", "493263553378385931"),
        IDLE("idle", "493263553101430795"),
        DND("dnd", "493263553260945419"),
        STREAMING("streaming", "313956277132853248"),
        DISCORD_PARTNER("partner", "314068430556758017"),
        DISCORD_HYPESQUAD("hypesquad", "314068430854684672"),
        DISCORD_NITRO("nitro", "314068430611415041"),
        DISCORD_STAFF("staff", "314068430787706880"),
        GIF("gif", "314068430624129039"),
        STAFF_TOOLS("stafftools", "314348604095594498"),
        YT("youtube", "314349922885566475"),
        TWITTER("twitter", "314349922877046786"),
        TWITCH("twitch", "314349922755411970"),
        DISCORD("discord", "314003252830011395"),
        CHECK("check", "314349398811475968"),
        X_CHECK("xmark", "314349398824058880"),
        EMPTY_CHECK("empty", "314349398723264512");

        private String name;
        private String id;

        Emotes(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Emote toEmote() {
            return DiscordUtils.getBaseGuild().getEmoteById(id);
        }

        public String toMention() {
            return "<:" + name + ":" + id + ">";
        }
    }

    private static final Color DEFAULT_COLOR = new Color(102, 167, 197);
    private static final Color ERROR_COLOR = new Color(244, 67, 54);
    private static final Color SUCCESS_COLOR = new Color(45, 206, 137);

    public static Message of(String title, String iconUrl, ColorType colorType, String[] body) {
        StringBuilder sb = new StringBuilder();
        if (body != null) {
            for (String input : body) {
                sb.append(input + "\n");
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(title, null, iconUrl);
        eb.setDescription(sb.toString().trim());
        return append(eb, colorType.getColor());
    }

    public static Message of(String title, Icons icon, ColorType colorType, String[] body) {
        return of(title, icon.getUrl(), colorType, body);
    }

    public static Message of(String title, Icons icon, Color color, String[] body) {
        StringBuilder sb = new StringBuilder();
        if (body != null) {
            for (String input : body) {
                sb.append(input + "\n");
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(title, null, icon.getUrl());
        eb.setDescription(sb.toString().trim());
        return append(eb, color);
    }

    public static Message of(String title, String iconUrl, String imageUrl, String footer, ColorType colorType, String[] body) {
        StringBuilder sb = new StringBuilder();
        if (body != null) {
            for (String input : body) {
                sb.append(input + "\n");
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(title, null, iconUrl);
        eb.setDescription(sb.toString().trim());
        eb.setFooter(footer, null);
        eb.setColor(colorType.getColor());
        eb.setThumbnail(imageUrl);
        MessageEmbed embed = eb.build();
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embed);
        return builder.build();
    }

    public static Message of(String title, String iconUrl, String imageUrl, ColorType colorType, String[] body) {
        StringBuilder sb = new StringBuilder();
        if (body != null) {
            for (String input : body) {
                sb.append(input + "\n");
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(title, null, iconUrl);
        eb.setDescription(sb.toString().trim());
        eb.setFooter("GamesBoat", null);
        eb.setColor(colorType.getColor());
        eb.setThumbnail(imageUrl);
        MessageEmbed embed = eb.build();
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embed);
        return builder.build();
    }

    public static Message ofImg(String title, Icons icon, String imageUrl, ColorType colorType, String[] body) {
        StringBuilder sb = new StringBuilder();
        if (body != null) {
            for (String input : body) {
                sb.append(input + "\n");
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(title, null, icon.getUrl());
        eb.setDescription(sb.toString().trim());
        eb.setFooter("GamesBoat", null);
        eb.setColor(colorType.getColor());
        eb.setThumbnail(imageUrl);
        MessageEmbed embed = eb.build();
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embed);
        return builder.build();
    }

    public static Message of(String title, Icons icon, String footer, ColorType colorType, String[] body) {
        return of(title, icon.getUrl(), footer, colorType.getColor(), body);
    }

    public static Message of(String title, String iconUrl, String footer, Color color, String[] body, LinkedList<MessageEmbed.Field> fields) {
        StringBuilder sb = new StringBuilder();
        if (body != null) {
            for (String input : body) {
                sb.append(input + "\n");
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(title, null, iconUrl);
        eb.setDescription(sb.toString().trim());
        eb.setFooter(footer, null);
        eb.setColor(color);
        if (fields != null) {
            for (MessageEmbed.Field field : fields) {
                eb.addField(field);
            }
        }
        MessageEmbed embed = eb.build();
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embed);
        return builder.build();
    }

    public static Message of(String title, String iconUrl, String footer, Color color, String[] body) {
        StringBuilder sb = new StringBuilder();
        if (body != null) {
            for (String input : body) {
                sb.append(input + "\n");
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(title, null, iconUrl);
        eb.setDescription(sb.toString().trim());
        eb.setFooter(footer, null);
        eb.setColor(color);
        MessageEmbed embed = eb.build();
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embed);
        return builder.build();
    }

    public static Message of(String title, Icons icon, String footer, ColorType color, String[] body, LinkedList<MessageEmbed.Field> fields) {
        StringBuilder sb = new StringBuilder();
        if (body != null) {
            for (String input : body) {
                sb.append(input + "\n");
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(title, null, icon.getUrl());
        eb.setDescription(sb.toString().trim());
        eb.setFooter(footer, null);
        eb.setColor(color.getColor());
        if (fields != null) {
            for (MessageEmbed.Field field : fields) {
                eb.addField(field);
            }
        }
        MessageEmbed embed = eb.build();
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embed);
        return builder.build();
    }

    public static Message of(String title, String iconUrl, ColorType colorType, String[] body, LinkedList<MessageEmbed.Field> fields) {
        return of(title, iconUrl, "GamesBoat", colorType.getColor(), body, fields);
    }

    public static Message of(String title, Icons icon, Color color, String[] body, LinkedList<MessageEmbed.Field> fields) {
        return of(title, icon.getUrl(), "GamesBoat", color, body, fields);
    }

    public static Message of(String title, Icons icon, ColorType colorType, String[] body, LinkedList<MessageEmbed.Field> fields) {
        return of(title, icon.getUrl(), colorType, body, fields);
    }

    public static Message confirm(String title, Icons icon, String action) {
        return of(title, icon.getUrl(), ColorType.RED, new String[] {
                "Please confirm you would like to " + action + ".",
                "Type " + DiscordUtils.code("y(es)") + " to confirm, or " + DiscordUtils.code("n(o)") + " to cancel."
        });
    }

    public static void notAuthorized(MessageReceivedEvent e, Member client) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Error", null, Icons.ERROR.getUrl());
        eb.setDescription("You are not authorized to use " + DiscordUtils.code(e.getMessage().getContentRaw())
                + "\n"
                + "\n" + client.getUser().getAsMention() + ", you must be a Radium developer to use this."
                + "\nLearn more at https://radium.pro/superperms"
                + "\nThis result of this action is not a mistake."
                + "\n");
        buildError(e, eb);
    }

    private static void buildError(MessageReceivedEvent e, EmbedBuilder eb) {
        eb.setFooter("GamesBoat", null);
        eb.setColor(ERROR_COLOR);
        MessageEmbed embed = eb.build();
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(embed);
        Message m = mb.build();
        e.getChannel().sendMessage(m).queue();
    }

    public static void ownerOnly(MessageReceivedEvent e, Member client) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Error", null, Icons.ERROR.getUrl());
        eb.setDescription("You don't have permission to use " + DiscordUtils.code(e.getMessage().getContentRaw())
                + "\n"
                + "\n" + client.getUser().getAsMention() + ", you must be the guild owner to perform this action."
                + "\nIf you believe this is a mistake, please contact a staff member."
                + "\n");
        buildError(e, eb);
    }

    public static Message notInitialized() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Error", null, Icons.ERROR.getUrl());
        eb.setDescription("Uninitialized Guild"
                + "\n"
                + "\nThis guild has not been initialized by it's owner."
                + "\nPlease have the owner run " + DiscordUtils.code("!init") + " and follow the setup process."
                + "\nIf you believe this is a mistake, please contact an argon member."
                + "\n");
        return append(eb, ERROR_COLOR);
    }

    public static Message ranksNotSetup() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Error", null, Icons.ERROR.getUrl());
        eb.setDescription("Uninitialized Ranks"
                + "\n"
                + "\nThis guild has not set up their rank system."
                + "\nPlease have the owner run " + DiscordUtils.code("!ranks") + " and follow the setup process."
                + "\nIf you believe this is a mistake, please contact an argon member."
                + "\n");
        return append(eb, ERROR_COLOR);
    }

    private static Message append(EmbedBuilder eb, Color errorColor) {
        eb.setFooter("GamesBoat", null);
        eb.setColor(errorColor);
        MessageEmbed embed = eb.build();
        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(embed);
        return mb.build();
    }

    public static Message help(CommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Help", null, Icons.COMMANDS.getUrl());
        eb.setDescription("Radium Help"
                + "\n"
                + "\nHi there, I see you want some help regarding how to use Radium."
                + "\nWe have a helpful page for such occasions, [click here](https://radium.pro/help) to visit it."
                + (event.getMember().isOwner() || event.getMember().hasPermission(Permission.ADMINISTRATOR) ? "\nIf you need help configuring Radium, you may want to visit our [setup guide](https://radium.pro/setup)." : "")
                + "\nIf you need any help beyond this point, you should contact a staff member."
                + "\n");
        return append(eb, DEFAULT_COLOR);
    }

}
