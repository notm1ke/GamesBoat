<h1 align="center">GamesBoat 🎮</h1>
<p align="center">
  <img src="https://img.shields.io/badge/version-0.1-blue.svg?cacheSeconds=2592000" />
  <img src="https://img.shields.io/github/license/argonclub/gamesboat.svg" />
</p>

> GamesBoat is a cross-server games bot created for Discord's first Hack Week.
Please note that this README is still incomplete, and will be periodically updated as we come closer to the submission deadline.

## Usage

```sh
First start up the bot using the following command. Doing this will force it to generate a preferences file that you may modify.
This file will contain everything from database credentials to user storage types. Once the file is generated, and you have configured it to your liking,
you may once again start up the bot using the below command.

java -jar GamesBoat-1.0-SNAPSHOT.jar
```

## Configuration
```sh
token:        The secret token used to authorize the bot with Discord.
storageType:  The type of storage system you would like to use (FLAT_FILE/MYSQL)

// All of the next section pertains to if you are using MySQL/MariaDB
database:     The database which should contain all data in.
host:         The hostname or IP of the target database server.
port:         The port that the database is running on.
user:         The user to login to the database as.
password:     The password to use to log in to the database server.

// All of the next section pertains to if you are using Flat File
dataRoot:     The root directory for the data to be stored in.

baseServer:   The Snowflake ID of the central server you want GamesBoat to run on.
wordListPath: The path to the JSON file containing all words used for Hangman.

```

## Authors

👤 [@notm1ke](https://github.com/notm1ke), [@Struck713](https://github.com/struck713), and [@EyeAmfour](https://github.com/EyeAmfour)

## Show your support

Give us a ⭐️ if you think this project was cool!

***