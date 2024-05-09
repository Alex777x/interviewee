#!/bin/bash

echo "Parse Kafka version from pom.xml"
KAFKA_VERSION=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='properties']/*[local-name()='kafka.version']/text()" pom.xml)

echo "Stop Kafka service"
./kafka_2.13-"$KAFKA_VERSION"/bin/kafka-server-stop.sh ./kafka_2.13-"$KAFKA_VERSION"/config/server.properties
