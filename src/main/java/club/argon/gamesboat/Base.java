package club.argon.gamesboat;

import club.argon.gamesboat.commands.terminal.StatusCommand;
import club.argon.gamesboat.commands.terminal.StopCommand;
import club.argon.gamesboat.game.GameManager;
import club.argon.gamesboat.storage.Preferences;
import club.argon.gamesboat.util.emote.selection.emoji.EmojiSelector;

import co.m1ke.basic.events.EventManager;
import co.m1ke.basic.events.listener.ListenerAdapter;
import co.m1ke.basic.logger.Logger;
import co.m1ke.basic.scheduler.SimpleScheduler;
import co.m1ke.basic.terminal.Terminal;
import co.m1ke.basic.utils.Lang;
import co.m1ke.basic.utils.timings.Timings;

import java.io.File;
import java.util.concurrent.Executors;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

public class Base {

    private Application application;
    private Logger logger;

    private static JDA jda;
    private EventWaiter waiter;
    private CommandClient client;

    private static Preferences preferences;
    private static GameManager gameManager;

    private static SimpleScheduler scheduler;
    private static EventManager eventManager;
    private static ListenerAdapter listenerAdapter;

    public Base(Application application) {
        this.application = application;
    }

    public void boot() {
        try {
            Timings timings = new Timings("GamesBoat", "<init>");
            this.logger = new Logger("GamesBoat");

            logger.raw(Lang.CYAN + "\n\t ██████╗  █████╗ ███╗   ███╗███████╗███████╗██████╗  ██████╗  █████╗ ████████╗\n" +
                    "\t██╔════╝ ██╔══██╗████╗ ████║██╔════╝██╔════╝██╔══██╗██╔═══██╗██╔══██╗╚══██╔══╝\n" +
                    "\t██║  ███╗███████║██╔████╔██║█████╗  ███████╗██████╔╝██║   ██║███████║   ██║   \n" +
                    "\t██║   ██║██╔══██║██║╚██╔╝██║██╔══╝  ╚════██║██╔══██╗██║   ██║██╔══██║   ██║   \n" +
                    "\t╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗███████║██████╔╝╚██████╔╝██║  ██║   ██║   \n" +
                    "\t ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝╚══════╝╚═════╝  ╚═════╝ ╚═╝  ╚═╝   ╚═╝   \n" +
                    "                                                                              " + Lang.RESET);
            logger.raw("\t\t      Booting " + Lang.CYAN + "GamesBoat" + Lang.RESET + " version " + Lang.WHITE + "#6/master (snapshot)" + Lang.RESET);
            logger.raw("\t\t\t      Made with " + Lang.RED + "<3" + Lang.RESET + " the Argon Team.");
            logger.raw("");

            preferences = new Preferences(new File("preferences.json"));

            this.waiter = new EventWaiter();
            this.client = new CommandClientBuilder()
                    .setPrefix(preferences.getPrefix())
                    .setOwnerId(preferences.getOwnerId())
                    .setScheduleExecutor(Executors.newScheduledThreadPool(50))
                    .setGame(preferences.getBotGame())
                    .build();
            jda = new JDABuilder()
                    .setToken(preferences.getToken())
                    .setStatus(OnlineStatus.ONLINE)
                    .setGame(Game.playing("loading up.."))
                    .addEventListener(waiter, client, new EmojiSelector())
                    .buildAsync()
                    .awaitReady();

            scheduler = new SimpleScheduler();
            eventManager = new EventManager(true);
            listenerAdapter = new ListenerAdapter();
            gameManager = new GameManager(jda, client, waiter);

            Terminal.initialize(application);

            // Override default stop command in favor our custom one.
            Terminal.getCommands().remove("stop");
            Terminal.register(new StatusCommand(application, jda, gameManager), new StopCommand(application, jda));

            timings.complete("Initialization took %c%tms" + Lang.RESET + ".");
        } catch (Exception e) {
            logger.except(e, "Error starting up");
            e.printStackTrace();
        }
    }

    public static JDA getAPI() {
        return jda;
    }

    public static Preferences getPreferences() {
        return preferences;
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
