#!/usr/bin/env bash
clear
echo 'Building GamesBoat..'
./gradlew shadowJar
echo 'Finished building GamesBoat'
cd dist/
echo 'Running GamesBoat'
java -jar GamesBoat-1.0-SNAPSHOT-all.jar