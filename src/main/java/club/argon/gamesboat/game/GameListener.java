package club.argon.gamesboat.game;

import club.argon.gamesboat.game.event.GameMessageEvent;
import club.argon.gamesboat.game.lobby.GameLobby;
import club.argon.gamesboat.util.Embeds;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GameListener extends ListenerAdapter {

    private GameManager gameManager;

    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User sender = event.getAuthor();

        if (sender.isBot() || sender.isFake() || event.isWebhookMessage()) {
            return;
        }

        if (event.getChannelType() == ChannelType.PRIVATE) {
            GameLobby lobby = gameManager.searchForUser(sender);
            if (lobby == null) {
                event.getChannel().sendMessage(Embeds.of("Private Message", Embeds.Icons.ERROR.getUrl(), Embeds.ColorType.RED, new String[] {
                        "Prototype will only use the private messaging channel when communicating with users who are active in games."
                })).queue();
                return;
            }
            GameMessageEvent gameEvent = new GameMessageEvent(lobby.getGame(), event, event.getMessage().getContentStripped());
            lobby.getGame().onEmit(gameEvent);
        }
    }
}
