#!/bin/bash

echo "Parse Kafka version from pom.xml"
KAFKA_VERSION=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='properties']/*[local-name()='kafka.version']/text()" pom.xml)
echo "Kafka version: $KAFKA_VERSION"

echo "Check if Kafka is already downloaded"
if [ ! -d "kafka_2.13-$KAFKA_VERSION" ]; then
    echo "Download Kafka"
    curl -O https://downloads.apache.org/kafka/"$KAFKA_VERSION"/kafka_2.13-"$KAFKA_VERSION".tgz

    echo "Extract Kafka"
    tar -xzf kafka_2.13-"$KAFKA_VERSION".tgz
fi

echo "Killing any processes using Zookeeper and Kafka ports"
ZOOKEEPER_PID=$(lsof -t -i :2181)
if [ ! -z "$ZOOKEEPER_PID" ]; then
    kill -9 $ZOOKEEPER_PID
fi

KAFKA_PID=$(lsof -t -i :9092)
if [ ! -z "$KAFKA_PID" ]; then
    kill -9 $KAFKA_PID
fi

# Wait for a moment to ensure Zookeeper and Kafka processes are killed
sleep 5

echo "Start Zookeeper service"
./kafka_2.13-"$KAFKA_VERSION"/bin/zookeeper-server-start.sh ./kafka_2.13-"$KAFKA_VERSION"/config/zookeeper.properties &
while ! nc -z localhost 2181; do sleep 1; done

echo "Removing lock file"
rm -f /tmp/kafka-logs/.lock

echo "Start Kafka service"
./kafka_2.13-"$KAFKA_VERSION"/bin/kafka-server-start.sh ./kafka_2.13-"$KAFKA_VERSION"/config/server.properties &

echo "Waiting for Kafka service to be ready..."
while ! nc -z localhost 9092; do sleep 1; done

topics=("question" "translated-question" "answer" "translated-answer")

echo "Create topics"
for topic in "${topics[@]}"
do
   # Check if the topic exists
   if ! ./kafka_2.13-"$KAFKA_VERSION"/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic "$topic" > /dev/null 2>&1; then
       # If the topic does not exist, create it
       ./kafka_2.13-"$KAFKA_VERSION"/bin/kafka-topics.sh --create --topic "$topic" --bootstrap-server localhost:9092
   fi
done
