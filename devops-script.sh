#!/bin/bash

ARTIFACT_FILENAME=bosses-plugin.jar
PLUGINS_DIR=/home/wilmer/hosts/minecraft-paper-server/plugins
OUTPUT_DIR=/home/wilmer/projects/bosses-plugin/out

# Watch for changes in the directory
inotifywait -m -r -e modify,create,delete $PLUGINS_DIR |
while read path action file; do
    cd $PLUGINS_DIR
    cd ../

    # Get the PID of the running java process
    PID=$(pgrep -f 'java -Xms6G -Xmx6G -jar paper.jar --nogui')

    # If the java process is running, kill it
    if [ -n "$PID" ]; then
        echo "Stopping java process (PID $PID)..."
        sudo kill "$PID"
    fi

    java -Xms6G -Xmx6G -jar paper.jar --nogui > /dev/tty < /dev/tty &
done
