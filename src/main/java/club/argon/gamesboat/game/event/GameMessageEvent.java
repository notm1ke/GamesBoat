package club.argon.gamesboat.game.event;

import club.argon.gamesboat.game.Game;

import co.m1ke.basic.events.interfaces.Event;

import java.lang.annotation.Annotation;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class GameMessageEvent implements Event {

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    private Game game;
    private MessageReceivedEvent event;
    private String message;

    public GameMessageEvent(Game game, MessageReceivedEvent event, String message) {
        this.game = game;
        this.event = event;
        this.message = message;
    }

    public Game getGame() {
        return game;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public User getUser() {
        return event.getAuthor();
    }

    public String getMessage() {
        return message;
    }

}
