package club.argon.gamesboat.storage.types;

import club.argon.gamesboat.player.Player;
import club.argon.gamesboat.storage.Storage;

import co.m1ke.basic.utils.Database;

import java.sql.ResultSet;

public class SqlStorage extends Storage {

    private Database database;

    public SqlStorage(Database.Preferences prefs) {
        this.database = new Database(prefs);
    }

    /**
     * WARNING:
     * This method is susceptible to SQL Injection.
     * Please migrate Database utility to std in Basic@0.3
     */
    @Override
    public Player getPlayer(String id) {
        ResultSet rs = database.query("SELECT * FROM players WHERE id=?");
        return null;
    }

}
