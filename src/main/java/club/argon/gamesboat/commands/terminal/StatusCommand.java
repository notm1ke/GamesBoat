package club.argon.gamesboat.commands.terminal;

import club.argon.gamesboat.game.GameManager;

import co.m1ke.basic.BasicService;
import co.m1ke.basic.terminal.TerminalCommand;
import co.m1ke.basic.utils.Lang;
import co.m1ke.basic.utils.TimeUtil;

import net.dv8tion.jda.core.JDA;

public class StatusCommand extends TerminalCommand {

    private JDA jda;
    private GameManager gameManager;

    public StatusCommand(BasicService service, JDA jda, GameManager gameManager) {
        super(service, new String[] { "status" }, Lang.usage("Services", "status"), "Services");
        this.jda = jda;
        this.gameManager = gameManager;
    }

    @Override
    public void execute(String[] args) {
        if (jda.getStatus() != JDA.Status.CONNECTED) {
            logger.warning("GamesBoat is not connected to Discord.");
            return;
        }
        logger.info("-- GamesBoat Status Report --");
        logger.info("JDA is currently " + Lang.GREEN + jda.getStatus().name().toLowerCase().replaceAll("_", " ") + Lang.RESET);
        logger.info(" - " + Lang.PURPLE + jda.getGuilds().size() + Lang.RESET + " guild" + TimeUtil.numberEnding(jda.getGuilds().size()) + " connected");
        logger.info(" - " + Lang.PURPLE + jda.getUsers().size() + Lang.RESET + " user" + TimeUtil.numberEnding(jda.getUsers().size()) + " tracked");
        logger.info(" - " + Lang.PURPLE + gameManager.getGames().size() + Lang.RESET + " game" + TimeUtil.numberEnding(gameManager.getGames().size()) + " running");
        logger.info(" - " + Lang.PURPLE + gameManager.getLobbies().size() + Lang.RESET + " lobb" + (gameManager.getLobbies().size() == 1 ? "y" : "ies") + " running");
        logger.info("-----------------------------");
    }

}
