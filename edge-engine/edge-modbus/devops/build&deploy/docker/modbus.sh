#!/bin/bash
### BEGIN INIT INFO
# Provides:          MODBUS_Indra_Service
# Short-Description: indra file
# Description:       indra configuration file
### END INIT INFO

echo "[INFO] Configuring TimeZone ${CONTAINER_TIMEZONE}"

# Set the timezone. Base image does not contain the setup-timezone script, so an alternate way is used.
cp /usr/share/zoneinfo/${CONTAINER_TIMEZONE} /etc/localtime && \
echo "${CONTAINER_TIMEZONE}" >  /etc/timezone && \
echo "[INFO] Container timezone set to: $CONTAINER_TIMEZONE"

#create link between ttyACM.. and ttyS.. used by u-blox wireless modem and Zigbee Chip
#rm -rf /var/lock/LCK..ttyS50
#rm -rf /var/lock/LCK..ttyS?*

#remove all LOCK serial files which are defined as a RTU devices into modbus,json config file (jq is required in container)
LCK_PATH="/var/lock/LCK.."

jq '.devices[] .serial'  /mnt/conf/modbus.json | while read line
do
   filename=$(basename "$line")
   lckfile=$LCK_PATH$filename
   lckfile=$(echo $lckfile | tr -d '"')
   echo "trying to delete LCK..file: "$lckfile
   rm $lckfile
done


echo "[INFO] Edge Modbus running!!"
java -jar $(ls /opt/modbus/modbus*.jar | xargs)
