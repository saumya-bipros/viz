package com.vizzionnaire.server.service.install.update;

public interface CacheCleanupService {

    void clearCache(String fromVersion) throws Exception;

}
