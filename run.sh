#!/usr/bin/env bash
gnome-terminal -e "docker run --rm -it -e SYSTEM_TYPE=EventManager pubsub"
gnome-terminal -e "docker run --rm -it -e SYSTEM_TYPE=PubSubAgent pubsub"

