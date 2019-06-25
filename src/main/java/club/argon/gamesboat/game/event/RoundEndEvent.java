package club.argon.gamesboat.game.event;

import club.argon.gamesboat.game.Game;

import co.m1ke.basic.events.interfaces.Event;

import java.lang.annotation.Annotation;

public class RoundEndEvent implements Event {

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    private Game game;
    private int round;
    private int max;

    public RoundEndEvent(Game game, int round, int max) {
        this.game = game;
        this.round = round;
        this.max = max;
    }

    public Game getGame() {
        return game;
    }

    public int getRound() {
        return round;
    }

    public int getMax() {
        return max;
    }

}
