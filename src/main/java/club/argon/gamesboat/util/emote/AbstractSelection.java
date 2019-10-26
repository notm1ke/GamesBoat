package club.argon.gamesboat.util.emote;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public abstract class AbstractSelection<T> {

    private Message message;
    private User author;
    private Guild guild;
    private MessageChannel channel;

    public AbstractSelection(Message msg, User ar) {
        this.message = msg;
        this.author = ar;
        this.guild = msg.getGuild();
        this.channel = msg.getChannel();
    }

    public AbstractSelection() {
        this.message = null;
        this.author = null;
        this.guild = null;
        this.channel = null;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public void setchannel(MessageChannel textchannel) {
        this.channel = textchannel;
    }

    /**
     * Check if this is a valid selection for Generics
     * @param event
     * @return
     */
    public abstract boolean isSelection(GenericMessageEvent event);

    public abstract int selector(String choice);

    protected boolean isSamePlace(Guild g, TextChannel tc) {
        return g.getId().equals(this.guild.getId()) && tc.getId().equals(this.channel.getId());
    }

    protected boolean isSameAuthor(User mem) {
        return mem.getId().equals(this.author.getId());
    }

}
