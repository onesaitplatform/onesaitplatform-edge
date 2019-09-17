#!/bin/bash
./mvnw install sonar:sonar -Dmaven.test.skip=true -Dsonar.projectVersion=$(date +%y.%m.%d) -Dsonar.host.url=http://192.168.1.101:9000 -Dsonar.login=02e8474b56032a06c1cc5cb9eb7f2e607090f260
