#!/bin/bash
./mvnw install sonar:sonar -Dmaven.test.skip=true -Dsonar.projectVersion=$(date +%y.%m.%d) -Dsonar.host.url=http://192.168.1.101:9000 -Dsonar.login=50a03dd6bd8f38a5fe7bcc6bf27e65bd57b239e2
