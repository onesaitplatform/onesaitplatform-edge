#!/bin/bash
### BEGIN INIT INFO
# Provides:          Zigbee_Indra_Service
# Short-Description: indra file
# Description:       indra configuration file
### END INIT INFO


VENDOR_ID_TEXAS="0451"
DEVICE_NAME_BASE="ttyACM"
N_ENLACES=0
MAX_ENLACES=1

echo "[INFO] Configuring TimeZone: ${CONTAINER_TIMEZONE}"

# Set the timezone. Base image does not contain the setup-timezone script, so an alternate way is used.
cp /usr/share/zoneinfo/${CONTAINER_TIMEZONE} /etc/localtime && \
echo "${CONTAINER_TIMEZONE}" >  /etc/timezone && \
echo "[INFO] Container timezone set to: $CONTAINER_TIMEZONE"

#create link between ttyACM.. and ttyS.. used by u-blox wireless modem and Zigbee Chip
rm -rf /var/lock/LCK..ttyS50
rm -rf /var/lock/LCK..ttyS?*
rm -rf /var/lock/LCK..ttyUSB*
rm -rf /dev/ttyS50

for sysdevpath in $(find /sys/bus/usb/devices/usb*/ -name dev); do
        syspath="${sysdevpath%/dev}"
        devname="$(udevadm info -q name -p $syspath)"
        [[ "$devname" == "bus/"* ]] && continue
        eval "$(udevadm info -q property --export -p $syspath)"
        [[ -z "$ID_MODEL" ]] && continue
	echo $devname
        if [ "$ID_VENDOR_ID" = "$VENDOR_ID_TEXAS" ]; then
             if [[ $devname == $DEVICE_NAME_BASE*   ]]; then
                echo "Found Zigbee chip  in /dev/$devname"
                   if [ "$MAX_ENLACES" -gt "$N_ENLACES" ]; then
                      echo "Creating simbolik link in /dev/$devname"
                      chmod 777 "/dev/$devname"
                      ln -s "/dev/$devname" "/dev/ttyS50"
                      ((N_ENLACES++))
                   fi
            fi
        fi
done

if [ "$N_ENLACES" -eq 0 ]; then
   #TTYACM=$(ls -l /dev/ttyACM* | awk '{print $10}')
   chmod 777 $TTYACM
   ln -s  "/dev/ttyACM0" "/dev/ttyS50"
   echo "[WARN]: Could not be obtained the info of /dev/. Check your docker-compose and make sure to find:"
   echo "- /run/udev:/run/udev:ro"
   echo "- /dev:/dev"
   echo "Forcing to create it in ttyACM0 to ttyS50"
   echo "Simbolik link created /dev/ttyS50 to $TTYACM"
   echo "Device list inside container"
   ls -la /dev/ttyACM*
fi
echo "ttyS* devices listed inside container"
ls -la /dev/ttyS*

echo "[INFO] Edge ZIGBEE running!!"
java -jar $(ls /opt/zigbee/zigbee*.jar | xargs)
