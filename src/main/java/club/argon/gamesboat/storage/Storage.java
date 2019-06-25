package club.argon.gamesboat.storage;

import club.argon.gamesboat.player.Player;

import co.m1ke.basic.logger.Logger;

public abstract class Storage {

    public abstract Player getPlayer(String id);

    public Logger getLogger() {
        return new Logger("Storage");
    }

}
