package club.argon.gamesboat.storage;

import club.argon.gamesboat.util.DiscordUtils;

import co.m1ke.basic.logger.Logger;
import co.m1ke.basic.utils.Database;
import co.m1ke.basic.utils.JsonUtils;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Preferences {

    private File file;
    private Logger logger;
    private JSONObject base;

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
            this.file = file;

            if (!file.exists()) {
                file.createNewFile();

                // TODO: Populate prefs file here
            }

            if (!file.exists()) {
                logger.severe("We were unable to create a preferences file.");
                logger.severe("Do you have the appropriate permissions for this directory?");
                return;
            }

            this.base = new JSONObject(FileUtils.readFileToString(file));
            this.storageType = StorageType.match(base.getString("storageType"));

            if (storageType == null) {
                logger.severe("We encountered an error while parsing your preferences file.");
                logger.severe("The storage type you specified, " + base.getString("storageType") + ", is not valid.");
                return;
            }

            // Load all MySQL related preferences
            if (storageType == StorageType.MYSQL) {
                JSONObject db = base.getJSONObject("database");
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
                    logger.info("Successfully read flat file data path.");
                } catch (JSONException e) {
                    error(e, "Error parsing flat file data path");
                    return;
                }
            }

            this.baseServer = base.getString("baseServer");

            if (!DiscordUtils.isSnowflake(this.baseServer)) {
                logger.severe("Base Server ID is not a valid Snowflake ID.");
                return;
            }

            this.wordListPath = base.getString("wordListPath");
            if (!new File(wordListPath).exists()) {
                logger.severe("Word List Path is not a valid path.");
                return;
            }

            try {
                JSONObject completeWordList = JsonUtils.getFromFile(new File(wordListPath));
                this.wordList = completeWordList.getJSONArray("words");
            } catch (JSONException e) {
                error(e, "Error parsing Word List");
                return;
            }

        } catch (Exception e) {
            error(e, "Error reading preferences file");
        }
    }
    
    private void error(Exception error, String base) {
        logger.except(error, base);
        System.exit(-1);
    }

}
