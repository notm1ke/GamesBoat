package club.argon.gamesboat.storage;

import club.argon.gamesboat.storage.error.PreferenceParsingError;
import club.argon.gamesboat.util.DiscordUtils;

import co.m1ke.basic.logger.Logger;
import co.m1ke.basic.utils.Database;
import co.m1ke.basic.utils.JsonUtils;
import co.m1ke.basic.utils.Lang;
import co.m1ke.basic.utils.timings.Timings;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

public class Preferences {

    /**
     * TODO:
     * - Add preference to toggle games on/off
     * - Add options for specific commands to require roles (eg you need a role to use !game)
     *
     */

    private File file;
    private Logger logger;
    private JSONObject base;

    private String token;
    private String prefix;
    private String ownerId;
    private OnlineStatus status;
    private Game botGame;
    private StorageType storageType;

    // One of these CAN be null, depending on which storage type is selected.
    private Database.Preferences databaseCredentials;
    private String flatFilePath;

    private String baseServer;
    private String wordListPath;
    private JSONArray wordList;

    public Preferences(File file) {
        this.logger = new Logger("Prefs");
        try {
            Timings timings = new Timings("Prefs", "Load Preferences");
            this.file = file;

            if (!file.exists()) {
                file.createNewFile();

                JSONObject defaultPrefs = new JSONObject()
                        .put("token", "")
                        .put("status", "")
                        .put("botGame", new JSONObject()
                                .put("type", "")
                                .put("name", ""))
                        .put("storageType", "")
                        .put("sql", new JSONObject()
                                .put("database", "")
                                .put("host", "")
                                .put("port", "")
                                .put("user", "")
                                .put("password", ""))
                        .put("flatFile", new JSONObject()
                                .put("dataRoot", "./data/"))
                        .put("baseServer", "")
                        .put("wordListPath", "./words.json");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file))) {
                    writer.write(defaultPrefs.toString(3));
                } catch (IOException e) {
                    logger.except(e, "Error writing default preferences to the file");
                    e.printStackTrace();
                    return;
                }
                logger.info("Finished writing default preferences to the file.");
                logger.info("Please configure the preferences to your liking, and then restart the bot.");
                System.exit(-1);
                return;
            }

            if (!file.exists()) {
                logger.severe("We were unable to create a preferences file.");
                logger.severe("Do you have the appropriate permissions for this directory?");
                return;
            }

            this.base = new JSONObject(FileUtils.readFileToString(file));
            this.token = base.getString("token");
            this.prefix = base.getString("prefix");
            this.ownerId = base.getString("ownerId");
            this.status = OnlineStatus.fromKey(base.getString("status"));
            this.botGame = Game.of(Game.GameType.fromKey(base.getJSONObject("botGame").getInt("type")), base.getJSONObject("botGame").getString("name"));
            this.storageType = StorageType.match(base.getString("storageType"));

            if (storageType == null) {
                logger.severe("We encountered an error while parsing your preferences file.");
                logger.severe("The storage type you specified, " + base.getString("storageType") + ", is not valid.");
                return;
            }

            // Load all MySQL related preferences
            if (storageType == StorageType.MYSQL) {
                JSONObject db = base.getJSONObject("sql");
                try {
                    this.databaseCredentials = new Database.Preferences(db.getString("database"), db.getString("host"), db.getString("port"), db.getString("user"), db.getString("password"));
                    logger.info("Successfully read database credentials.");
                } catch (JSONException e) {
                    // catches not found strings
                    error(e, "Error parsing database credentials");
                    return;
                }
            }

            // Load all flat-file related preferences
            if (storageType == StorageType.FLAT_FILE) {
                JSONObject ff = base.getJSONObject("flatFile");
                try {
                    this.flatFilePath = ff.getString("dataRoot");
                } catch (JSONException e) {
                    error(e, "Error parsing flat file data path");
                    return;
                }
            }

            this.baseServer = base.getString("baseServer");

            if (!DiscordUtils.isSnowflake(this.baseServer)) {
                logger.severe("Base Server ID is not a valid Snowflake ID.");
                throw new PreferenceParsingError();
            }

            this.wordListPath = base.getString("wordListPath");
            if (!new File(wordListPath).exists()) {
                logger.severe("Word List Path is not a valid path.");
                throw new PreferenceParsingError();
            }

            try {
                JSONObject completeWordList = JsonUtils.getFromFile(new File(wordListPath));
                this.wordList = completeWordList.getJSONArray("words");
            } catch (JSONException e) {
                error(e, "Error parsing Word List");
                return;
            }

            timings.complete("Preferences loaded in %c%tms" + Lang.RESET + ".");
        } catch (Exception e) {
            error(e, "Error reading preferences file");
        }
    }
    
    private void error(Exception error, String base) {
        logger.except(error, base);
        logger.severe("Printing Stacktrace:");
        error.printStackTrace();
        logger.severe("Finished printing stacktrace.");
        System.exit(-1);
    }

    public String getToken() {
        return token;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public Game getBotGame() {
        return botGame;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public Database.Preferences getDatabaseCredentials() {
        return databaseCredentials;
    }

    public String getFlatFilePath() {
        return flatFilePath;
    }

    public String getBaseServer() {
        return baseServer;
    }

    public String getWordListPath() {
        return wordListPath;
    }

    public JSONArray getWordList() {
        return wordList;
    }

}
