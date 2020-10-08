#!/bin/bash

set -e

./gradlew clean war

rsync -av web/build/libs/covid-plz-web-0.0.1-production.war \
root@web1.topobyte.de:/opt/tomcat/tomcat-secure/rootapps/covid-plz/ROOT.war
