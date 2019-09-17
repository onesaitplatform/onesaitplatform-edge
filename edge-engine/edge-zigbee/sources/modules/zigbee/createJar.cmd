call mvnw install:install-file -Dfile=lib/com.onesait.edge.engine.rxtx.native.jar -DgroupId=com.onesait.edge.engine -DartifactId=rxtx-native -Dversion=1.0.0 -Dpackaging=jar
call mvnw install:install-file -Dfile=lib/seroUtils.jar -DgroupId=com.serotonin -DartifactId=serotonin-utils -Dversion=2.1.7 -Dpackaging=jar
call mvnw clean
call echo "Please (in eclipse), Force Maven update in project for rebuild maven dependencies (Alt+F5)"

mvnw.cmd package -DskipTests -DbuildDirectory=%userprofile%/onesait-edge-platform/zigbee/target -X
