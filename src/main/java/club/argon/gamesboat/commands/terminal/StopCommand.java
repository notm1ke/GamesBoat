package club.argon.gamesboat.commands.terminal;

import co.m1ke.basic.BasicService;
import co.m1ke.basic.terminal.TerminalCommand;
import co.m1ke.basic.utils.Lang;

import net.dv8tion.jda.core.JDA;

public class StopCommand extends TerminalCommand {

    private JDA jda;

    public StopCommand(BasicService service, JDA jda) {
        super(service, new String[] { "stop", "shutdown", "end" }, Lang.usage("Services", "stop"), "Services");
        this.jda = jda;
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 0) {
            logger.info(getHelp());
            return;
        }

        jda.shutdown();
        logger.info("GamesBoat service deactivated.");

        System.exit(0);
    }

}
