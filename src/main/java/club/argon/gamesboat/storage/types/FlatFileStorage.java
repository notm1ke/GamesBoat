package club.argon.gamesboat.storage.types;

import club.argon.gamesboat.Base;
import club.argon.gamesboat.player.Player;
import club.argon.gamesboat.storage.Storage;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class FlatFileStorage extends Storage {

    private File file;
    private JSONObject base;

    public FlatFileStorage(File file) {
        try {
            this.file = file;
            this.base = new JSONObject(FileUtils.readFileToString(file));
        } catch (Exception e) {
            getLogger().except(e, "Error reading flat file storage");
        }
    }

    @Override
    public Player getPlayer(String id) {
        JSONArray players = base.getJSONArray("players");
        if (players.length() == 0) {
            return null;
        }

        for (Object objectz : players.toList()) {
            JSONObject cur = (JSONObject) objectz;
            if (!cur.getString("id").equalsIgnoreCase(id)) {
                continue;
            }
            return new Player(Base.getAPI().getUserById(cur.getString("id")), cur.getInt("coins"));
        }

        return null;
    }

}
