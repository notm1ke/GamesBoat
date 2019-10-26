package club.argon.gamesboat.util.emote.selection.emoji;

import club.argon.gamesboat.util.emote.selection.EmojiSelection;

import java.util.HashMap;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class EmojiSelector extends ListenerAdapter {

    private static HashMap<String, EmojiSelection> emojiSelector = new HashMap<String, EmojiSelection>();

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (emojiSelector.containsKey(event.getUser().getId())) {
            EmojiSelection selection = emojiSelector.get(event.getUser().getId());
            if (selection.isSelection(event)) {
                selection.action(selection.selector(event.getReactionEmote().getName()));
                emojiSelector.remove(event.getUser().getId());
            }
        }
    }

    public static void addEmojiSelection(String author, EmojiSelection select) {
        if (select.getMessage().isFromType(ChannelType.PRIVATE) || select.getGuild().getSelfMember().hasPermission((Channel) select.getChannel(), Permission.MESSAGE_ADD_REACTION)) {
            for (String em : select.getOption()) {
                if (select.getMessage() == null) {
                    continue;
                }
                select.getMessage().addReaction(em).queue();
            }
            emojiSelector.put(author, select);
        }
    }

}