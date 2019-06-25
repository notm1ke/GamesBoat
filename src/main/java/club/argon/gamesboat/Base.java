package club.argon.gamesboat;

import club.argon.gamesboat.game.GameManager;
import club.argon.gamesboat.scheduler.SimpleRunnableImpl;
import club.argon.gamesboat.util.emote.selection.emoji.EmojiSelector;

import co.m1ke.basic.events.EventManager;
import co.m1ke.basic.events.listener.ListenerAdapter;
import co.m1ke.basic.logger.Logger;
import co.m1ke.basic.scheduler.SimpleScheduler;
import co.m1ke.basic.utils.Lang;
import co.m1ke.basic.utils.timings.Timings;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

public class Base {

    private Logger logger;

    private static JDA jda;
    private EventWaiter waiter;
    private CommandClient client;

    private static GameManager gameManager;

    private static SimpleScheduler scheduler;
    private static EventManager eventManager;
    private static ListenerAdapter listenerAdapter;

    public void boot() {
        try {
            Timings timings = new Timings("GamesBoat", "<init>");
            this.logger = new Logger("GamesBoat");

            this.waiter = new EventWaiter();
            this.client = new CommandClientBuilder()
                    .setPrefix("!")
                    .setOwnerId("177167251986841600")
                    .setScheduleExecutor(Executors.newScheduledThreadPool(50))
                    .setGame(Game.playing("on Argon"))
                    .build();
            jda = new JDABuilder()
                    .setToken("NTkyNzYxODAyOTg3NjY3NDc2.XREChw.FS-m6CAq_l2axK_Pelxh1GMeKsc") // move this to preferences file before we open source it
                    .setStatus(OnlineStatus.ONLINE)
                    .setGame(Game.playing("loading up.."))
                    .addEventListener(waiter, client, new EmojiSelector())
                    .buildAsync();
            scheduler = new SimpleScheduler();
            eventManager = new EventManager(true);
            listenerAdapter = new ListenerAdapter();

            // Delay task in order to allow JDA time to connect.
            scheduler.scheduleDelayedTask(new SimpleRunnableImpl() {
                @Override
                public void run() {
                    gameManager = new GameManager(jda, client, waiter);
                }
            }, 1500L, TimeUnit.MILLISECONDS);

            timings.complete("Initialization task took %c%tms" + Lang.RESET + ".");
        } catch (Exception e) {
            logger.except(e, "Error starting up");
            e.printStackTrace();
        }
    }

    public static JDA getAPI() {
        return jda;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static SimpleScheduler getScheduler() {
        return scheduler;
    }

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static ListenerAdapter getListenerAdapter() {
        return listenerAdapter;
    }
}
