#!/bin/sh

#Add local libraries in .m2 folder

./mvnw install:install-file -Dfile=src/main/resources/lib/com.onesait.edge.engine.rxtx.native.jar -DgroupId=com.onesait.edge.engine -DartifactId=rxtx-native -Dversion=1.0.0 -Dpackaging=jar
./mvnw install:install-file -Dfile=src/main/resources/lib/seroUtils.jar -DgroupId=com.serotonin -DartifactId=serotonin-utils -Dversion=2.1.7 -Dpackaging=jar
./mvnw clean
echo "Please (in eclipse), Force Maven update in project for rebuild maven dependencies (Alt+F5)"

#Create Jar

./mvnw package -DskipTests -DbuildDirectory=~/onesait-edge-platform/zigbee/target -X
