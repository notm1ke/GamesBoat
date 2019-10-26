<h1 align="center">GamesBoat 🎮</h1>
<p align="center">
  <img src="https://img.shields.io/badge/version-0.1-blue.svg?cacheSeconds=2592000" />
  <img src="https://img.shields.io/github/license/notm1ke/gamesboat.svg" />
</p>

> GamesBoat is a cross-server games bot created for Discord's first Hack Week.
Please note that this README is still incomplete, and will be periodically updated as we come closer to the submission deadline.

## Usage

```
First start up the bot using the following command.
Doing this will force it to generate a preferences file that you may modify.
This file will contain everything from database credentials to user storage types.
Once the file is generated, and you have configured it to your liking,
you may once again start up the bot using the below command.

java -jar GamesBoat-1.0-SNAPSHOT.jar
```

## Building from source

```
Compile Tool (macOS + Linux):
./compile

Gradle CLI (macOS + Linux):
./gradlew shadowJar

Gradle CLI (Windows):
gradlew.bat shadowJar
```

## Configuration

```
token:        The secret token used to authorize the bot with Discord.
status:       The status of the Bot (ONLINE, INVISIBLE, OFFLINE, IDLE, DO_NOT_DISTURB)
botGame: {
    type:     0 -> 2 | DEFAULT, STREAMING, LISTENING (in that order)
    name:     The textual representation of what the bot is doing (Playing ...)
}
storageType:  The storage system you would like to use (FLAT_FILE/MYSQL)

sql: {
    database: The database which should contain all data in.
    host:     The hostname or IP of the target database server.
    port:     The port that the database is running on.
    user:     The user to login to the database as.
    password: The password to use when logging to the database server.
}

flatFile: {
    dataRoot: The root directory for the data to be stored in.
}

baseServer:   The Snowflake ID of the central server you want GamesBoat to run on.
wordListPath: The path to the JSON file containing all words used for Hangman.
```

## What we didn't finish
> Although we were pretty ambitious about what we could do, not everything we planned came to be. Here is a list of unfinished stuff that never made it into the bot.
- User System / Statistics
- Leaderboards
- Hangman Game
- Text-based Paintball Game

## Authors

👤 [@notm1ke](https://github.com/notm1ke), [@Struck713](https://github.com/struck713), and [@EyeAmfour](https://github.com/EyeAmfour)
> Please note that I, notm1ke, am solely maintaining this repository, however, all of us are collectively programming the bot using a live collaboration plugin. Therefore, I commit and push our code at the end of each programming session.

## Show your support

Give us a ⭐️ if you think this project was cool!

***