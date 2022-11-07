package com.vizzionnaire.server.dao.service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.DeviceProfileType;
import com.vizzionnaire.server.common.data.DeviceTransportType;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.OtaPackage;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.device.profile.DefaultDeviceProfileConfiguration;
import com.vizzionnaire.server.common.data.device.profile.DefaultDeviceProfileTransportConfiguration;
import com.vizzionnaire.server.common.data.device.profile.DeviceProfileData;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.event.RuleNodeDebugEvent;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.HasId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.ota.ChecksumAlgorithm;
import com.vizzionnaire.server.common.data.ota.OtaPackageType;
import com.vizzionnaire.server.dao.alarm.AlarmService;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.audit.AuditLogLevelFilter;
import com.vizzionnaire.server.dao.audit.AuditLogLevelMask;
import com.vizzionnaire.server.dao.audit.AuditLogLevelProperties;
import com.vizzionnaire.server.dao.component.ComponentDescriptorService;
import com.vizzionnaire.server.dao.customer.CustomerService;
import com.vizzionnaire.server.dao.dashboard.DashboardService;
import com.vizzionnaire.server.dao.device.DeviceCredentialsService;
import com.vizzionnaire.server.dao.device.DeviceProfileService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.dao.edge.EdgeEventService;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.dao.entity.EntityService;
import com.vizzionnaire.server.dao.entityview.EntityViewService;
import com.vizzionnaire.server.dao.event.EventService;
import com.vizzionnaire.server.dao.ota.OtaPackageService;
import com.vizzionnaire.server.dao.queue.QueueService;
import com.vizzionnaire.server.dao.relation.RelationService;
import com.vizzionnaire.server.dao.resource.ResourceService;
import com.vizzionnaire.server.dao.rpc.RpcService;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.dao.settings.AdminSettingsService;
import com.vizzionnaire.server.dao.tenant.TenantProfileService;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.dao.timeseries.TimeseriesService;
import com.vizzionnaire.server.dao.usagerecord.ApiUsageStateService;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.dao.widget.WidgetTypeService;
import com.vizzionnaire.server.dao.widget.WidgetsBundleService;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AbstractServiceTest.class, loader = AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Configuration
@ComponentScan("com.vizzionnaire.server")
public abstract class AbstractServiceTest {

    protected ObjectMapper mapper = new ObjectMapper();

    public static final TenantId SYSTEM_TENANT_ID = TenantId.SYS_TENANT_ID;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ApiUsageStateService apiUsageStateService;

    @Autowired
    protected AdminSettingsService adminSettingsService;

    @Autowired
    protected TenantService tenantService;

    @Autowired
    protected CustomerService customerService;

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected AssetService assetService;

    @Autowired
    protected EntityViewService entityViewService;

    @Autowired
    protected EntityService entityService;

    @Autowired
    protected DeviceCredentialsService deviceCredentialsService;

    @Autowired
    protected WidgetsBundleService widgetsBundleService;

    @Autowired
    protected WidgetTypeService widgetTypeService;

    @Autowired
    protected DashboardService dashboardService;

    @Autowired
    protected TimeseriesService tsService;

    @Autowired
    protected EventService eventService;

    @Autowired
    protected RelationService relationService;

    @Autowired
    protected AlarmService alarmService;

    @Autowired
    protected RuleChainService ruleChainService;

    @Autowired
    protected EdgeService edgeService;

    @Autowired
    protected EdgeEventService edgeEventService;

    @Autowired
    private ComponentDescriptorService componentDescriptorService;

    @Autowired
    protected TenantProfileService tenantProfileService;

    @Autowired
    protected DeviceProfileService deviceProfileService;

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected OtaPackageService otaPackageService;

    @Autowired
    protected RpcService rpcService;

    @Autowired
    protected QueueService queueService;

    public class IdComparator<D extends HasId> implements Comparator<D> {
        @Override
        public int compare(D o1, D o2) {
            return o1.getId().getId().compareTo(o2.getId().getId());
        }
    }


    protected RuleNodeDebugEvent generateEvent(TenantId tenantId, EntityId entityId) throws IOException {
        if (tenantId == null) {
            tenantId = TenantId.fromUUID(Uuids.timeBased());
        }
        return RuleNodeDebugEvent.builder()
                .tenantId(tenantId)
                .entityId(entityId.getId())
                .serviceId("server A")
                .data(JacksonUtil.toString(readFromResource("TestJsonData.json")))
                .build();
    }
//
//    private ComponentDescriptor getOrCreateDescriptor(ComponentScope scope, ComponentType type, String clazz, String configurationDescriptorResource) throws IOException {
//        return getOrCreateDescriptor(scope, type, clazz, configurationDescriptorResource, null);
//    }
//
//    private ComponentDescriptor getOrCreateDescriptor(ComponentScope scope, ComponentType type, String clazz, String configurationDescriptorResource, String actions) throws IOException {
//        ComponentDescriptor descriptor = componentDescriptorService.findByClazz(clazz);
//        if (descriptor == null) {
//            descriptor = new ComponentDescriptor();
//            descriptor.setName("test");
//            descriptor.setClazz(clazz);
//            descriptor.setScope(scope);
//            descriptor.setType(type);
//            descriptor.setActions(actions);
//            descriptor.setConfigurationDescriptor(readFromResource(configurationDescriptorResource));
//            componentDescriptorService.saveComponent(descriptor);
//        }
//        return descriptor;
//    }

    public JsonNode readFromResource(String resourceName) throws IOException {
        return mapper.readTree(this.getClass().getClassLoader().getResourceAsStream(resourceName));
    }

    @Bean
    public AuditLogLevelFilter auditLogLevelFilter() {
        Map<String, String> mask = new HashMap<>();
        for (EntityType entityType : EntityType.values()) {
            mask.put(entityType.name().toLowerCase(), AuditLogLevelMask.RW.name());
        }
        var props = new AuditLogLevelProperties();
        props.setMask(mask);
        return new AuditLogLevelFilter(props);
    }

    protected DeviceProfile createDeviceProfile(TenantId tenantId, String name) {
        DeviceProfile deviceProfile = new DeviceProfile();
        deviceProfile.setTenantId(tenantId);
        deviceProfile.setName(name);
        deviceProfile.setType(DeviceProfileType.DEFAULT);
        deviceProfile.setTransportType(DeviceTransportType.DEFAULT);
        deviceProfile.setDescription(name + " Test");
        DeviceProfileData deviceProfileData = new DeviceProfileData();
        DefaultDeviceProfileConfiguration configuration = new DefaultDeviceProfileConfiguration();
        DefaultDeviceProfileTransportConfiguration transportConfiguration = new DefaultDeviceProfileTransportConfiguration();
        deviceProfileData.setConfiguration(configuration);
        deviceProfileData.setTransportConfiguration(transportConfiguration);
        deviceProfile.setProfileData(deviceProfileData);
        deviceProfile.setDefault(false);
        deviceProfile.setDefaultRuleChainId(null);
        return deviceProfile;
    }

    public TenantId createTenant() {
        Tenant tenant = new Tenant();
        tenant.setTitle("My tenant " + Uuids.timeBased());
        Tenant savedTenant = tenantService.saveTenant(tenant);
        assertNotNull(savedTenant);
        return savedTenant.getId();
    }

    protected Edge constructEdge(TenantId tenantId, String name, String type) {
        Edge edge = new Edge();
        edge.setTenantId(tenantId);
        edge.setName(name);
        edge.setType(type);
        edge.setSecret(StringUtils.randomAlphanumeric(20));
        edge.setRoutingKey(StringUtils.randomAlphanumeric(20));
        return edge;
    }

    protected OtaPackage constructDefaultOtaPackage(TenantId tenantId, DeviceProfileId deviceProfileId) {
        OtaPackage firmware = new OtaPackage();
        firmware.setTenantId(tenantId);
        firmware.setDeviceProfileId(deviceProfileId);
        firmware.setType(OtaPackageType.FIRMWARE);
        firmware.setTitle("My firmware");
        firmware.setVersion("3.3.3");
        firmware.setFileName("filename.txt");
        firmware.setContentType("text/plain");
        firmware.setChecksumAlgorithm(ChecksumAlgorithm.SHA256);
        firmware.setChecksum("4bf5122f344554c53bde2ebb8cd2b7e3d1600ad631c385a5d7cce23c7785459a");
        firmware.setData(ByteBuffer.wrap(new byte[]{1}));
        firmware.setDataSize(1L);
        return firmware;
    }

}
