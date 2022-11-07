package com.vizzionnaire.server.service.install;

public interface DatabaseEntitiesUpgradeService {

    void upgradeDatabase(String fromVersion) throws Exception;

}
