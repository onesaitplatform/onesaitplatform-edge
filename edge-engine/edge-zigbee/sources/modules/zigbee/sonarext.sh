#!/bin/bash
./mvnw install sonar:sonar -Dmaven.test.skip=true -Dsonar.projectVersion=$(date +%y.%m.%d) -Dsonar.host.url=http://81.34.97.60:2916 -Dsonar.login=02e8474b56032a06c1cc5cb9eb7f2e607090f260
