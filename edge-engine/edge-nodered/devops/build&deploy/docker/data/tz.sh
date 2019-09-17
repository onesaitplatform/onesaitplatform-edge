#!/bin/sh

echo "[INFO] Configuring TimeZone ${TIMEZONE}"

# Set the timezone. Base image does not contain the setup-timezone script, so an alternate way is used.
cp /usr/share/zoneinfo/${TIMEZONE} /etc/localtime && \
echo "${TIMEZONE}" >  /etc/timezone && \
echo "[INFO] Container timezone set to: $TIMEZONE"

