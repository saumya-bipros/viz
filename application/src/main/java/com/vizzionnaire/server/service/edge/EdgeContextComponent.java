package com.vizzionnaire.server.service.edge;

import freemarker.template.Configuration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.attributes.AttributesService;
import com.vizzionnaire.server.dao.dashboard.DashboardService;
import com.vizzionnaire.server.dao.device.DeviceProfileService;
import com.vizzionnaire.server.dao.edge.EdgeEventService;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.dao.ota.OtaPackageService;
import com.vizzionnaire.server.dao.queue.QueueService;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.dao.settings.AdminSettingsService;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.dao.widget.WidgetsBundleService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.edge.rpc.EdgeEventStorageSettings;
import com.vizzionnaire.server.service.edge.rpc.processor.AdminSettingsEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.AlarmEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.AssetEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.CustomerEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.DashboardEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.DeviceEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.DeviceProfileEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.EntityEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.EntityViewEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.OtaPackageEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.QueueEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.RelationEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.RuleChainEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.TelemetryEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.UserEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.WidgetBundleEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.WidgetTypeEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.sync.EdgeRequestsService;
import com.vizzionnaire.server.service.executors.DbCallbackExecutorService;
import com.vizzionnaire.server.service.executors.GrpcCallbackExecutorService;

@Component
@TbCoreComponent
@Data
@Lazy
public class EdgeContextComponent {

    @Autowired
    private TbClusterService clusterService;

    @Autowired
    private EdgeService edgeService;

    @Autowired
    private EdgeEventService edgeEventService;

    @Autowired
    private AdminSettingsService adminSettingsService;

    @Autowired
    private Configuration freemarkerConfig;

    @Autowired
    private AssetService assetService;

    @Autowired
    private DeviceProfileService deviceProfileService;

    @Autowired
    private AttributesService attributesService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private RuleChainService ruleChainService;

    @Autowired
    private UserService userService;

    @Autowired
    private WidgetsBundleService widgetsBundleService;

    @Autowired
    private EdgeRequestsService edgeRequestsService;

    @Autowired
    private OtaPackageService otaPackageService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private AlarmEdgeProcessor alarmProcessor;

    @Autowired
    private DeviceProfileEdgeProcessor deviceProfileProcessor;

    @Autowired
    private DeviceEdgeProcessor deviceProcessor;

    @Autowired
    private EntityEdgeProcessor entityProcessor;

    @Autowired
    private AssetEdgeProcessor assetProcessor;

    @Autowired
    private EntityViewEdgeProcessor entityViewProcessor;

    @Autowired
    private UserEdgeProcessor userProcessor;

    @Autowired
    private RelationEdgeProcessor relationProcessor;

    @Autowired
    private TelemetryEdgeProcessor telemetryProcessor;

    @Autowired
    private DashboardEdgeProcessor dashboardProcessor;

    @Autowired
    private RuleChainEdgeProcessor ruleChainProcessor;

    @Autowired
    private CustomerEdgeProcessor customerProcessor;

    @Autowired
    private WidgetBundleEdgeProcessor widgetBundleProcessor;

    @Autowired
    private WidgetTypeEdgeProcessor widgetTypeProcessor;

    @Autowired
    private AdminSettingsEdgeProcessor adminSettingsProcessor;

    @Autowired
    private OtaPackageEdgeProcessor otaPackageEdgeProcessor;

    @Autowired
    private QueueEdgeProcessor queueEdgeProcessor;

    @Autowired
    private EdgeEventStorageSettings edgeEventStorageSettings;

    @Autowired
    private DbCallbackExecutorService dbCallbackExecutor;

    @Autowired
    private GrpcCallbackExecutorService grpcCallbackExecutorService;
}
