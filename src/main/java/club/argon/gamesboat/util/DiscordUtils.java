package club.argon.gamesboat.util;

import club.argon.gamesboat.Base;

import co.m1ke.basic.logger.Logger;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.utils.Checks;

public class DiscordUtils {

    public static Guild getBaseGuild() {
        return Base.getAPI().getGuildById("470798258877497366");
    }

    public static GuildController getBaseController() {
        return getBaseGuild().getController();
    }

    public static final Logger JDA_LOGGER = new Logger("JDA");

    public static boolean isSnowflake(String input) {
        try {
            Checks.isSnowflake(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Member getMentionedMember(MessageReceivedEvent e) {
        return getMentionedMember(e, 0);
    }

    public static boolean containsMention(MessageReceivedEvent e) {
        return e.getMessage().getMentionedUsers().size() >= 1;
    }

    public static Member getMentionedMember(MessageReceivedEvent e, int index) {
        try {
            return e.getMessage().getMentionedMembers().get(index);
        } catch (IndexOutOfBoundsException exception) {
            return null;
        }
    }

    public static String getFullName(User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String getImage(Message message) {
        return message.getAttachments().get(0).getUrl();
    }

    public static boolean containsImage(Message message) {
        if (message.getAttachments().stream().anyMatch(Message.Attachment::isImage)) {
            return true;
        }
        return message.getEmbeds().stream().anyMatch(e -> e.getImage() != null || e.getVideoInfo() != null);
    }

    public static String escape(String value) {
        return value
                .replace("*", "\\*")
                .replace("_", "\\_")
                .replace("`", "\\`");
    }

    public static String underline(String value) {
        return "__" + escape(value) + "__";
    }

    public static String slant(String value) {
        return "_" + escape(value) + "_";
    }

    public static String bold(String value) {
        return "**" + escape(value) + "**";
    }

    public static String code(String value) {
        return "`" + escape(value) + "`";
    }

    public static PrivateChannel getPrivateChannel(User user) {
        try {
            if (user.isBot()) {
                return null;
            }
            return user.openPrivateChannel().complete();
        } catch (UnsupportedOperationException e) {
            JDA_LOGGER.except(e, "Error opening private channel");
            return null;
        }
    }

    public static Message getPendingMessage(CommandEvent event, Message message) {
        return event.getChannel().sendMessage(message).complete();
    }

    public static String emojiToUnicode(String discordLocalized) {
        return EmojiParser.parseToUnicode(discordLocalized);
    }

}
