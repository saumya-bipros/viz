package com.vizzionnaire.server.service.install;

public interface DatabaseTsUpgradeService {

    void upgradeDatabase(String fromVersion) throws Exception;

}