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

echo "Start Zookeeper service"
./kafka_2.13-"$KAFKA_VERSION"/bin/zookeeper-server-start.sh ./kafka_2.13-"$KAFKA_VERSION"/config/zookeeper.properties &
while ! nc -z localhost 2181; do sleep 1; done

echo "Start Kafka service"
./kafka_2.13-"$KAFKA_VERSION"/bin/kafka-server-start.sh ./kafka_2.13-"$KAFKA_VERSION"/config/server.properties &
while ! nc -z localhost 9092; do sleep 1; done

# Define topics
topics=("question" "translated-question" "answer" "translated-answer")

echo "Create topics"
for topic in "${topics[@]}"
do
   ./kafka_2.13-"$KAFKA_VERSION"/bin/kafka-topics.sh --create --topic "$topic" --bootstrap-server localhost:9092
done
