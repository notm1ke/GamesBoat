package club.argon.gamesboat.game.event;

import club.argon.gamesboat.game.Game;

import co.m1ke.basic.events.interfaces.Event;

import java.lang.annotation.Annotation;

import net.dv8tion.jda.core.entities.User;

public class GameQuitEvent implements Event {

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    private Game game;
    private User user;

    public GameQuitEvent(Game game, User user) {
        this.game = game;
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public User getUser() {
        return user;
    }

}
