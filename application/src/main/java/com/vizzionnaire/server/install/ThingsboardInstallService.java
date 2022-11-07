package com.vizzionnaire.server.install;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.service.component.ComponentDiscoveryService;
import com.vizzionnaire.server.service.install.DatabaseEntitiesUpgradeService;
import com.vizzionnaire.server.service.install.DatabaseTsUpgradeService;
import com.vizzionnaire.server.service.install.EntityDatabaseSchemaService;
import com.vizzionnaire.server.service.install.SystemDataLoaderService;
import com.vizzionnaire.server.service.install.TsDatabaseSchemaService;
import com.vizzionnaire.server.service.install.TsLatestDatabaseSchemaService;
import com.vizzionnaire.server.service.install.migrate.EntitiesMigrateService;
import com.vizzionnaire.server.service.install.migrate.TsLatestMigrateService;
import com.vizzionnaire.server.service.install.update.CacheCleanupService;
import com.vizzionnaire.server.service.install.update.DataUpdateService;

@Service
@Profile("install")
@Slf4j
public class ThingsboardInstallService {

    @Value("${install.upgrade:false}")
    private Boolean isUpgrade;

    @Value("${install.upgrade.from_version:1.2.3}")
    private String upgradeFromVersion;

    @Value("${install.load_demo:false}")
    private Boolean loadDemo;

    @Autowired
    private EntityDatabaseSchemaService entityDatabaseSchemaService;

    @Autowired
    private TsDatabaseSchemaService tsDatabaseSchemaService;

    @Autowired(required = false)
    private TsLatestDatabaseSchemaService tsLatestDatabaseSchemaService;

    @Autowired
    private DatabaseEntitiesUpgradeService databaseEntitiesUpgradeService;

    @Autowired(required = false)
    private DatabaseTsUpgradeService databaseTsUpgradeService;

    @Autowired
    private ComponentDiscoveryService componentDiscoveryService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SystemDataLoaderService systemDataLoaderService;

    @Autowired
    private DataUpdateService dataUpdateService;

    @Autowired
    private CacheCleanupService cacheCleanupService;

    @Autowired(required = false)
    private EntitiesMigrateService entitiesMigrateService;

    @Autowired(required = false)
    private TsLatestMigrateService latestMigrateService;

    public void performInstall() {
        try {
            if (isUpgrade) {
                log.info("Starting ThingsBoard Upgrade from version {} ...", upgradeFromVersion);

                cacheCleanupService.clearCache(upgradeFromVersion);

                if ("2.5.0-cassandra".equals(upgradeFromVersion)) {
                    log.info("Migrating ThingsBoard entities data from cassandra to SQL database ...");
                    entitiesMigrateService.migrate();
                    log.info("Updating system data...");
                    systemDataLoaderService.updateSystemWidgets();
                } else if ("3.0.1-cassandra".equals(upgradeFromVersion)) {
                    log.info("Migrating ThingsBoard latest timeseries data from cassandra to SQL database ...");
                    latestMigrateService.migrate();
                } else {
                    switch (upgradeFromVersion) {
                        case "1.2.3": //NOSONAR, Need to execute gradual upgrade starting from upgradeFromVersion
                            log.info("Upgrading ThingsBoard from version 1.2.3 to 1.3.0 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("1.2.3");

                        case "1.3.0":  //NOSONAR, Need to execute gradual upgrade starting from upgradeFromVersion
                            log.info("Upgrading ThingsBoard from version 1.3.0 to 1.3.1 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("1.3.0");

                        case "1.3.1": //NOSONAR, Need to execute gradual upgrade starting from upgradeFromVersion
                            log.info("Upgrading ThingsBoard from version 1.3.1 to 1.4.0 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("1.3.1");

                        case "1.4.0":
                            log.info("Upgrading ThingsBoard from version 1.4.0 to 2.0.0 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("1.4.0");

                            dataUpdateService.updateData("1.4.0");

                        case "2.0.0":
                            log.info("Upgrading ThingsBoard from version 2.0.0 to 2.1.1 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("2.0.0");

                        case "2.1.1":
                            log.info("Upgrading ThingsBoard from version 2.1.1 to 2.1.2 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("2.1.1");
                        case "2.1.3":
                            log.info("Upgrading ThingsBoard from version 2.1.3 to 2.2.0 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("2.1.3");

                        case "2.3.0":
                            log.info("Upgrading ThingsBoard from version 2.3.0 to 2.3.1 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("2.3.0");

                        case "2.3.1":
                            log.info("Upgrading ThingsBoard from version 2.3.1 to 2.4.0 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("2.3.1");

                        case "2.4.0":
                            log.info("Upgrading ThingsBoard from version 2.4.0 to 2.4.1 ...");

                        case "2.4.1":
                            log.info("Upgrading ThingsBoard from version 2.4.1 to 2.4.2 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("2.4.1");
                        case "2.4.2":
                            log.info("Upgrading ThingsBoard from version 2.4.2 to 2.4.3 ...");

                            databaseEntitiesUpgradeService.upgradeDatabase("2.4.2");

                        case "2.4.3":
                            log.info("Upgrading ThingsBoard from version 2.4.3 to 2.5 ...");

                            if (databaseTsUpgradeService != null) {
                                databaseTsUpgradeService.upgradeDatabase("2.4.3");
                            }
                            databaseEntitiesUpgradeService.upgradeDatabase("2.4.3");
                        case "2.5.0":
                            log.info("Upgrading ThingsBoard from version 2.5.0 to 2.5.1 ...");
                            if (databaseTsUpgradeService != null) {
                                databaseTsUpgradeService.upgradeDatabase("2.5.0");
                            }
                        case "2.5.1":
                            log.info("Upgrading ThingsBoard from version 2.5.1 to 3.0.0 ...");
                        case "3.0.1":
                            log.info("Upgrading ThingsBoard from version 3.0.1 to 3.1.0 ...");
                            databaseEntitiesUpgradeService.upgradeDatabase("3.0.1");
                            dataUpdateService.updateData("3.0.1");
                        case "3.1.0":
                            log.info("Upgrading ThingsBoard from version 3.1.0 to 3.1.1 ...");
                            databaseEntitiesUpgradeService.upgradeDatabase("3.1.0");
                        case "3.1.1":
                            log.info("Upgrading ThingsBoard from version 3.1.1 to 3.2.0 ...");
                            if (databaseTsUpgradeService != null) {
                                databaseTsUpgradeService.upgradeDatabase("3.1.1");
                            }
                            databaseEntitiesUpgradeService.upgradeDatabase("3.1.1");
                            dataUpdateService.updateData("3.1.1");
                            systemDataLoaderService.createOAuth2Templates();
                        case "3.2.0":
                            log.info("Upgrading ThingsBoard from version 3.2.0 to 3.2.1 ...");
                            databaseEntitiesUpgradeService.upgradeDatabase("3.2.0");
                        case "3.2.1":
                            log.info("Upgrading ThingsBoard from version 3.2.1 to 3.2.2 ...");
                            if (databaseTsUpgradeService != null) {
                                databaseTsUpgradeService.upgradeDatabase("3.2.1");
                            }
                            databaseEntitiesUpgradeService.upgradeDatabase("3.2.1");
                        case "3.2.2":
                            log.info("Upgrading ThingsBoard from version 3.2.2 to 3.3.0 ...");
                            if (databaseTsUpgradeService != null) {
                                databaseTsUpgradeService.upgradeDatabase("3.2.2");
                            }
                            databaseEntitiesUpgradeService.upgradeDatabase("3.2.2");

                            dataUpdateService.updateData("3.2.2");
                            systemDataLoaderService.createOAuth2Templates();
                        case "3.3.0":
                            log.info("Upgrading ThingsBoard from version 3.3.0 to 3.3.1 ...");
                        case "3.3.1":
                            log.info("Upgrading ThingsBoard from version 3.3.1 to 3.3.2 ...");
                        case "3.3.2":
                            log.info("Upgrading ThingsBoard from version 3.3.2 to 3.3.3 ...");
                            databaseEntitiesUpgradeService.upgradeDatabase("3.3.2");
                            dataUpdateService.updateData("3.3.2");
                        case "3.3.3":
                            log.info("Upgrading ThingsBoard from version 3.3.3 to 3.3.4 ...");
                            databaseEntitiesUpgradeService.upgradeDatabase("3.3.3");
                        case "3.3.4":
                            log.info("Upgrading ThingsBoard from version 3.3.4 to 3.4.0 ...");
                            databaseEntitiesUpgradeService.upgradeDatabase("3.3.4");
                            dataUpdateService.updateData("3.3.4");
                        case "3.4.0":
                            log.info("Upgrading ThingsBoard from version 3.4.0 to 3.4.1 ...");
                            databaseEntitiesUpgradeService.upgradeDatabase("3.4.0");
                            dataUpdateService.updateData("3.4.0");
                            log.info("Updating system data...");
                            systemDataLoaderService.updateSystemWidgets();
                            break;

                        //TODO update CacheCleanupService on the next version upgrade

                        default:
                            throw new RuntimeException("Unable to upgrade ThingsBoard, unsupported fromVersion: " + upgradeFromVersion);

                    }
                }
                log.info("Upgrade finished successfully!");

            } else {

                log.info("Starting ThingsBoard Installation...");

                log.info("Installing DataBase schema for entities...");

                entityDatabaseSchemaService.createDatabaseSchema();

                log.info("Installing DataBase schema for timeseries...");

                tsDatabaseSchemaService.createDatabaseSchema();

                if (tsLatestDatabaseSchemaService != null) {
                    tsLatestDatabaseSchemaService.createDatabaseSchema();
                }

                log.info("Loading system data...");

                componentDiscoveryService.discoverComponents();

                systemDataLoaderService.createSysAdmin();
                systemDataLoaderService.createDefaultTenantProfiles();
                systemDataLoaderService.createAdminSettings();
                systemDataLoaderService.loadSystemWidgets();
                systemDataLoaderService.createOAuth2Templates();
                systemDataLoaderService.createQueues();
//                systemDataLoaderService.loadSystemPlugins();
//                systemDataLoaderService.loadSystemRules();

                if (loadDemo) {
                    log.info("Loading demo data...");
                    systemDataLoaderService.loadDemoData();
                }
                log.info("Installation finished successfully!");
            }


        } catch (Exception e) {
            log.error("Unexpected error during ThingsBoard installation!", e);
            throw new ThingsboardInstallException("Unexpected error during ThingsBoard installation!", e);
        } finally {
            SpringApplication.exit(context);
        }
    }

}
