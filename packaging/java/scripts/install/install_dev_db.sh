#!/bin/bash

BASE=${project.basedir}/target
CONF_FOLDER=${BASE}/conf
jarfile="${BASE}/vizzionnaire-${project.version}-boot.jar"
installDir=${BASE}/data
loadDemo=true


export JAVA_OPTS="$JAVA_OPTS -Dplatform=@pkg.platform@"
export LOADER_PATH=${BASE}/conf,${BASE}/extensions
export SQL_DATA_FOLDER=${SQL_DATA_FOLDER:-/tmp}


run_user="$USER"

sudo -u "$run_user" -s /bin/sh -c "java -cp ${jarfile} $JAVA_OPTS -Dloader.main=com.vizzionnaire.server.VizzionnaireInstallApplication \
                    -Dinstall.data_dir=${installDir} \
                    -Dinstall.load_demo=${loadDemo} \
                    -Dspring.jpa.hibernate.ddl-auto=none \
                    -Dinstall.upgrade=false \
                    -Dlogging.config=logback.xml \
                    org.springframework.boot.loader.PropertiesLauncher"

if [ $? -ne 0 ]; then
    echo "Vizzionnaire DB installation failed!"
else
    echo "Vizzionnaire DB installed successfully!"
fi

exit $?
