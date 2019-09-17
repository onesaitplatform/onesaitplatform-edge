#!/bin/bash
### BEGIN INIT INFO
# Provides:          KAFKA_Indra_Service
# Short-Description: indra file
# Description:       indra configuration file
### END INIT INFO

echo "[INFO] Configuring TimeZone ${CONTAINER_TIMEZONE}"

# Set the timezone. Base image does not contain the setup-timezone script, so an alternate way is used.
cp /usr/share/zoneinfo/${CONTAINER_TIMEZONE} /etc/localtime && \
echo "${CONTAINER_TIMEZONE}" >  /etc/timezone && \
echo "[INFO] Container timezone set to: $CONTAINER_TIMEZONE"

echo "[INFO] Edge kafka running!!"
java -jar $(ls /opt/kafka/kafka*.jar | xargs)
