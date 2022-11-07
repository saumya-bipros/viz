#!/bin/bash
start-db.sh

CONF_FOLDER="${pkg.installFolder}/conf"
jarfile=${pkg.installFolder}/bin/${pkg.name}.jar
configfile=${pkg.name}.conf
firstlaunch=${DATA_FOLDER}/.firstlaunch

source "${CONF_FOLDER}/${configfile}"

if [ ! -f ${firstlaunch} ]; then
    install-tb.sh --loadDemo && touch ${firstlaunch}
fi

if [ -f ${firstlaunch} ]; then
    echo "Starting Vizzionnaire ..."

    java -cp ${jarfile} $JAVA_OPTS -Dloader.main=com.vizzionnaire.server.VizzionnaireServerApplication \
                        -Dspring.jpa.hibernate.ddl-auto=none \
                        -Dlogging.config=${CONF_FOLDER}/logback.xml \
                        org.springframework.boot.loader.PropertiesLauncher
else
    echo "ERROR: Vizzionnaire is not installed"
fi

stop-db.sh