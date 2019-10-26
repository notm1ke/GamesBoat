package club.argon.gamesboat;

import co.m1ke.basic.BasicService;
import co.m1ke.basic.scheduler.SimpleScheduler;

public class Application implements BasicService {

    private static Base base;

    public static void main(String[] args) {
        base = new Base(new Application());
        base.boot();
    }

    @Override
    public String name() {
        return "GamesBoat";
    }

    @Override
    public String author() {
        return "Argon";
    }

    @Override
    public double version() {
        return 0.1;
    }

    @Override
    public SimpleScheduler getScheduler() {
        return Base.getScheduler();
    }

}
