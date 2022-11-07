package com.vizzionnaire.server.actors.ruleChain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.common.util.ListeningExecutor;
import com.vizzionnaire.rule.engine.api.MailService;
import com.vizzionnaire.rule.engine.api.RuleEngineAlarmService;
import com.vizzionnaire.rule.engine.api.RuleEngineDeviceProfileCache;
import com.vizzionnaire.rule.engine.api.RuleEngineRpcService;
import com.vizzionnaire.rule.engine.api.RuleEngineTelemetryService;
import com.vizzionnaire.rule.engine.api.ScriptEngine;
import com.vizzionnaire.rule.engine.api.SmsService;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbRelationTypes;
import com.vizzionnaire.rule.engine.api.sms.SmsSenderFactory;
import com.vizzionnaire.server.actors.ActorSystemContext;
import com.vizzionnaire.server.actors.TbActorRef;
import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.DataConstants;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rule.RuleNode;
import com.vizzionnaire.server.common.data.rule.RuleNodeState;
import com.vizzionnaire.server.common.msg.TbActorMsg;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;
import com.vizzionnaire.server.common.msg.TbMsgProcessingStackItem;
import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.common.msg.queue.TopicPartitionInfo;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.attributes.AttributesService;
import com.vizzionnaire.server.dao.cassandra.CassandraCluster;
import com.vizzionnaire.server.dao.customer.CustomerService;
import com.vizzionnaire.server.dao.dashboard.DashboardService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.dao.edge.EdgeEventService;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.dao.entityview.EntityViewService;
import com.vizzionnaire.server.dao.nosql.CassandraStatementTask;
import com.vizzionnaire.server.dao.nosql.TbResultSetFuture;
import com.vizzionnaire.server.dao.ota.OtaPackageService;
import com.vizzionnaire.server.dao.queue.QueueService;
import com.vizzionnaire.server.dao.relation.RelationService;
import com.vizzionnaire.server.dao.resource.ResourceService;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.dao.timeseries.TimeseriesService;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.TbQueueCallback;
import com.vizzionnaire.server.queue.TbQueueMsgMetadata;
import com.vizzionnaire.server.service.script.RuleNodeJsScriptEngine;

import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by ashvayka on 19.03.18.
 */
@Slf4j
class DefaultTbContext implements TbContext {

    public final static ObjectMapper mapper = new ObjectMapper();

    private final ActorSystemContext mainCtx;
    private final String ruleChainName;
    private final RuleNodeCtx nodeCtx;

    public DefaultTbContext(ActorSystemContext mainCtx, String ruleChainName, RuleNodeCtx nodeCtx) {
        this.mainCtx = mainCtx;
        this.ruleChainName = ruleChainName;
        this.nodeCtx = nodeCtx;
    }

    @Override
    public void tellSuccess(TbMsg msg) {
        tellNext(msg, Collections.singleton(TbRelationTypes.SUCCESS), null);
    }

    @Override
    public void tellNext(TbMsg msg, String relationType) {
        tellNext(msg, Collections.singleton(relationType), null);
    }

    @Override
    public void tellNext(TbMsg msg, Set<String> relationTypes) {
        tellNext(msg, relationTypes, null);
    }

    private void tellNext(TbMsg msg, Set<String> relationTypes, Throwable th) {
        if (nodeCtx.getSelf().isDebugMode()) {
            relationTypes.forEach(relationType -> mainCtx.persistDebugOutput(nodeCtx.getTenantId(), nodeCtx.getSelf().getId(), msg, relationType, th));
        }
        msg.getCallback().onProcessingEnd(nodeCtx.getSelf().getId());
        nodeCtx.getChainActor().tell(new RuleNodeToRuleChainTellNextMsg(nodeCtx.getSelf().getRuleChainId(), nodeCtx.getSelf().getId(), relationTypes, msg, th != null ? th.getMessage() : null));
    }

    @Override
    public void tellSelf(TbMsg msg, long delayMs) {
        //TODO: add persistence layer
        scheduleMsgWithDelay(new RuleNodeToSelfMsg(this, msg), delayMs, nodeCtx.getSelfActor());
    }

    @Override
    public void input(TbMsg msg, RuleChainId ruleChainId) {
        msg.pushToStack(nodeCtx.getSelf().getRuleChainId(), nodeCtx.getSelf().getId());
        nodeCtx.getChainActor().tell(new RuleChainInputMsg(ruleChainId, msg));
    }

    @Override
    public void output(TbMsg msg, String relationType) {
        TbMsgProcessingStackItem item = msg.popFormStack();
        if (item == null) {
            ack(msg);
        } else {
            if (nodeCtx.getSelf().isDebugMode()) {
                mainCtx.persistDebugOutput(nodeCtx.getTenantId(), nodeCtx.getSelf().getId(), msg, relationType);
            }
            nodeCtx.getChainActor().tell(new RuleChainOutputMsg(item.getRuleChainId(), item.getRuleNodeId(), relationType, msg));
        }
    }

    @Override
    public void enqueue(TbMsg tbMsg, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = mainCtx.resolve(ServiceType.TB_RULE_ENGINE, getTenantId(), tbMsg.getOriginator());
        enqueue(tpi, tbMsg, onFailure, onSuccess);
    }

    @Override
    public void enqueue(TbMsg tbMsg, String queueName, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg, queueName);
        enqueue(tpi, tbMsg, onFailure, onSuccess);
    }

    private void enqueue(TopicPartitionInfo tpi, TbMsg tbMsg, Consumer<Throwable> onFailure, Runnable onSuccess) {
        if (!tbMsg.isValid()) {
            log.trace("[{}] Skip invalid message: {}", getTenantId(), tbMsg);
            if (onFailure != null) {
                onFailure.accept(new IllegalArgumentException("Source message is no longer valid!"));
            }
            return;
        }
        TransportProtos.ToRuleEngineMsg msg = TransportProtos.ToRuleEngineMsg.newBuilder()
                .setTenantIdMSB(getTenantId().getId().getMostSignificantBits())
                .setTenantIdLSB(getTenantId().getId().getLeastSignificantBits())
                .setTbMsg(TbMsg.toByteString(tbMsg)).build();
        if (nodeCtx.getSelf().isDebugMode()) {
            mainCtx.persistDebugOutput(nodeCtx.getTenantId(), nodeCtx.getSelf().getId(), tbMsg, "To Root Rule Chain");
        }
        mainCtx.getClusterService().pushMsgToRuleEngine(tpi, tbMsg.getId(), msg, new SimpleTbQueueCallback(onSuccess, onFailure));
    }

    @Override
    public void enqueueForTellFailure(TbMsg tbMsg, String failureMessage) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, Collections.singleton(TbRelationTypes.FAILURE), failureMessage, null, null);
    }

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, String relationType) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, Collections.singleton(relationType), null, null, null);
    }

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, Set<String> relationTypes) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, relationTypes, null, null, null);
    }

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, String relationType, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, Collections.singleton(relationType), null, onSuccess, onFailure);
    }

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, Set<String> relationTypes, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg);
        enqueueForTellNext(tpi, tbMsg, relationTypes, null, onSuccess, onFailure);
    }

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, String queueName, String relationType, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg, queueName);
        enqueueForTellNext(tpi, queueName, tbMsg, Collections.singleton(relationType), null, onSuccess, onFailure);
    }

    @Override
    public void enqueueForTellNext(TbMsg tbMsg, String queueName, Set<String> relationTypes, Runnable onSuccess, Consumer<Throwable> onFailure) {
        TopicPartitionInfo tpi = resolvePartition(tbMsg, queueName);
        enqueueForTellNext(tpi, queueName, tbMsg, relationTypes, null, onSuccess, onFailure);
    }

    private TopicPartitionInfo resolvePartition(TbMsg tbMsg, String queueName) {
        return mainCtx.resolve(ServiceType.TB_RULE_ENGINE, queueName, getTenantId(), tbMsg.getOriginator());
    }

    private TopicPartitionInfo resolvePartition(TbMsg tbMsg) {
        return resolvePartition(tbMsg, tbMsg.getQueueName());
    }

    private void enqueueForTellNext(TopicPartitionInfo tpi, TbMsg source, Set<String> relationTypes, String failureMessage, Runnable onSuccess, Consumer<Throwable> onFailure) {
        enqueueForTellNext(tpi, source.getQueueName(), source, relationTypes, failureMessage, onSuccess, onFailure);
    }

    private void enqueueForTellNext(TopicPartitionInfo tpi, String queueName, TbMsg source, Set<String> relationTypes, String failureMessage, Runnable onSuccess, Consumer<Throwable> onFailure) {
        if (!source.isValid()) {
            log.trace("[{}] Skip invalid message: {}", getTenantId(), source);
            if (onFailure != null) {
                onFailure.accept(new IllegalArgumentException("Source message is no longer valid!"));
            }
            return;
        }
        RuleChainId ruleChainId = nodeCtx.getSelf().getRuleChainId();
        RuleNodeId ruleNodeId = nodeCtx.getSelf().getId();
        TbMsg tbMsg = TbMsg.newMsg(source, queueName, ruleChainId, ruleNodeId);
        TransportProtos.ToRuleEngineMsg.Builder msg = TransportProtos.ToRuleEngineMsg.newBuilder()
                .setTenantIdMSB(getTenantId().getId().getMostSignificantBits())
                .setTenantIdLSB(getTenantId().getId().getLeastSignificantBits())
                .setTbMsg(TbMsg.toByteString(tbMsg))
                .addAllRelationTypes(relationTypes);
        if (failureMessage != null) {
            msg.setFailureMessage(failureMessage);
        }
        if (nodeCtx.getSelf().isDebugMode()) {
            relationTypes.forEach(relationType ->
                    mainCtx.persistDebugOutput(nodeCtx.getTenantId(), nodeCtx.getSelf().getId(), tbMsg, relationType, null, failureMessage));
        }
        mainCtx.getClusterService().pushMsgToRuleEngine(tpi, tbMsg.getId(), msg.build(), new SimpleTbQueueCallback(onSuccess, onFailure));
    }

    @Override
    public void ack(TbMsg tbMsg) {
        if (nodeCtx.getSelf().isDebugMode()) {
            mainCtx.persistDebugOutput(nodeCtx.getTenantId(), nodeCtx.getSelf().getId(), tbMsg, "ACK", null);
        }
        tbMsg.getCallback().onProcessingEnd(nodeCtx.getSelf().getId());
        tbMsg.getCallback().onSuccess();
    }

    @Override
    public boolean isLocalEntity(EntityId entityId) {
        return mainCtx.resolve(ServiceType.TB_RULE_ENGINE, getTenantId(), entityId).isMyPartition();
    }

    private void scheduleMsgWithDelay(TbActorMsg msg, long delayInMs, TbActorRef target) {
        mainCtx.scheduleMsgWithDelay(target, msg, delayInMs);
    }

    @Override
    public void tellFailure(TbMsg msg, Throwable th) {
        if (nodeCtx.getSelf().isDebugMode()) {
            mainCtx.persistDebugOutput(nodeCtx.getTenantId(), nodeCtx.getSelf().getId(), msg, TbRelationTypes.FAILURE, th);
        }
        String failureMessage;
        if (th != null) {
            if (!StringUtils.isEmpty(th.getMessage())) {
                failureMessage = th.getMessage();
            } else {
                failureMessage = th.getClass().getSimpleName();
            }
        } else {
            failureMessage = null;
        }
        nodeCtx.getChainActor().tell(new RuleNodeToRuleChainTellNextMsg(nodeCtx.getSelf().getRuleChainId(),
                nodeCtx.getSelf().getId(), Collections.singleton(TbRelationTypes.FAILURE),
                msg, failureMessage));
    }

    public void updateSelf(RuleNode self) {
        nodeCtx.setSelf(self);
    }

    @Override
    public TbMsg newMsg(String queueName, String type, EntityId originator, TbMsgMetaData metaData, String data) {
        return newMsg(queueName, type, originator, null, metaData, data);
    }

    @Override
    public TbMsg newMsg(String queueName, String type, EntityId originator, CustomerId customerId, TbMsgMetaData metaData, String data) {
        return TbMsg.newMsg(queueName, type, originator, customerId, metaData, data, nodeCtx.getSelf().getRuleChainId(), nodeCtx.getSelf().getId());
    }

    @Override
    public TbMsg transformMsg(TbMsg origMsg, String type, EntityId originator, TbMsgMetaData metaData, String data) {
        return TbMsg.transformMsg(origMsg, type, originator, metaData, data);
    }

    public TbMsg customerCreatedMsg(Customer customer, RuleNodeId ruleNodeId) {
        return entityActionMsg(customer, customer.getId(), ruleNodeId, DataConstants.ENTITY_CREATED);
    }

    public TbMsg deviceCreatedMsg(Device device, RuleNodeId ruleNodeId) {
        RuleChainId ruleChainId = null;
         String queueName = null;
        if (device.getDeviceProfileId() != null) {
            DeviceProfile deviceProfile = mainCtx.getDeviceProfileCache().find(device.getDeviceProfileId());
            if (deviceProfile == null) {
                log.warn("[{}] Device profile is null!", device.getDeviceProfileId());
            } else {
                ruleChainId = deviceProfile.getDefaultRuleChainId();
                queueName = deviceProfile.getDefaultQueueName();
            }
        }
        return entityActionMsg(device, device.getId(), ruleNodeId, DataConstants.ENTITY_CREATED, queueName, ruleChainId);
    }

    public TbMsg assetCreatedMsg(Asset asset, RuleNodeId ruleNodeId) {
        return entityActionMsg(asset, asset.getId(), ruleNodeId, DataConstants.ENTITY_CREATED);
    }

    public TbMsg alarmActionMsg(Alarm alarm, RuleNodeId ruleNodeId, String action) {
        RuleChainId ruleChainId = null;
        String queueName = null;
        if (EntityType.DEVICE.equals(alarm.getOriginator().getEntityType())) {
            DeviceId deviceId = new DeviceId(alarm.getOriginator().getId());
            DeviceProfile deviceProfile = mainCtx.getDeviceProfileCache().get(getTenantId(), deviceId);
            if (deviceProfile == null) {
                log.warn("[{}] Device profile is null!", deviceId);
            } else {
                ruleChainId = deviceProfile.getDefaultRuleChainId();
                queueName = deviceProfile.getDefaultQueueName();
            }
        }
        return entityActionMsg(alarm, alarm.getId(), ruleNodeId, action, queueName, ruleChainId);
    }

    @Override
    public void onEdgeEventUpdate(TenantId tenantId, EdgeId edgeId) {
        mainCtx.getClusterService().onEdgeEventUpdate(tenantId, edgeId);
    }

    public <E, I extends EntityId> TbMsg entityActionMsg(E entity, I id, RuleNodeId ruleNodeId, String action) {
        return entityActionMsg(entity, id, ruleNodeId, action, null, null);
    }

    public <E, I extends EntityId> TbMsg entityActionMsg(E entity, I id, RuleNodeId ruleNodeId, String action, String queueName, RuleChainId ruleChainId) {
        try {
            return TbMsg.newMsg(queueName, action, id, getActionMetaData(ruleNodeId), mapper.writeValueAsString(mapper.valueToTree(entity)), ruleChainId, null);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new RuntimeException("Failed to process " + id.getEntityType().name().toLowerCase() + " " + action + " msg: " + e);
        }
    }

    @Override
    public RuleNodeId getSelfId() {
        return nodeCtx.getSelf().getId();
    }

    @Override
    public RuleNode getSelf() {
        return nodeCtx.getSelf();
    }

    @Override
    public String getRuleChainName() {
        return ruleChainName;
    }

    @Override
    public TenantId getTenantId() {
        return nodeCtx.getTenantId();
    }

    @Override
    public ListeningExecutor getMailExecutor() {
        return mainCtx.getMailExecutor();
    }

    @Override
    public ListeningExecutor getSmsExecutor() {
        return mainCtx.getSmsExecutor();
    }

    @Override
    public ListeningExecutor getDbCallbackExecutor() {
        return mainCtx.getDbCallbackExecutor();
    }

    @Override
    public ListeningExecutor getExternalCallExecutor() {
        return mainCtx.getExternalCallExecutorService();
    }

    @Override
    public ScriptEngine createJsScriptEngine(String script, String... argNames) {
        return new RuleNodeJsScriptEngine(getTenantId(), mainCtx.getJsSandbox(), nodeCtx.getSelf().getId(), script, argNames);
    }

    @Override
    public void logJsEvalRequest() {
        if (mainCtx.isStatisticsEnabled()) {
            mainCtx.getJsInvokeStats().incrementRequests();
        }
    }

    @Override
    public void logJsEvalResponse() {
        if (mainCtx.isStatisticsEnabled()) {
            mainCtx.getJsInvokeStats().incrementResponses();
        }
    }

    @Override
    public void logJsEvalFailure() {
        if (mainCtx.isStatisticsEnabled()) {
            mainCtx.getJsInvokeStats().incrementFailures();
        }
    }

    @Override
    public String getServiceId() {
        return mainCtx.getServiceInfoProvider().getServiceId();
    }

    @Override
    public AttributesService getAttributesService() {
        return mainCtx.getAttributesService();
    }

    @Override
    public CustomerService getCustomerService() {
        return mainCtx.getCustomerService();
    }

    @Override
    public TenantService getTenantService() {
        return mainCtx.getTenantService();
    }

    @Override
    public UserService getUserService() {
        return mainCtx.getUserService();
    }

    @Override
    public AssetService getAssetService() {
        return mainCtx.getAssetService();
    }

    @Override
    public DeviceService getDeviceService() {
        return mainCtx.getDeviceService();
    }

    @Override
    public TbClusterService getClusterService() {
        return mainCtx.getClusterService();
    }

    @Override
    public DashboardService getDashboardService() {
        return mainCtx.getDashboardService();
    }

    @Override
    public RuleEngineAlarmService getAlarmService() {
        return mainCtx.getAlarmService();
    }

    @Override
    public RuleChainService getRuleChainService() {
        return mainCtx.getRuleChainService();
    }

    @Override
    public TimeseriesService getTimeseriesService() {
        return mainCtx.getTsService();
    }

    @Override
    public RuleEngineTelemetryService getTelemetryService() {
        return mainCtx.getTsSubService();
    }

    @Override
    public RelationService getRelationService() {
        return mainCtx.getRelationService();
    }

    @Override
    public EntityViewService getEntityViewService() {
        return mainCtx.getEntityViewService();
    }

    @Override
    public ResourceService getResourceService() {
        return mainCtx.getResourceService();
    }

    @Override
    public OtaPackageService getOtaPackageService() {
        return mainCtx.getOtaPackageService();
    }

    @Override
    public RuleEngineDeviceProfileCache getDeviceProfileCache() {
        return mainCtx.getDeviceProfileCache();
    }

    @Override
    public EdgeService getEdgeService() {
        return mainCtx.getEdgeService();
    }

    @Override
    public EdgeEventService getEdgeEventService() {
        return mainCtx.getEdgeEventService();
    }

    @Override
    public QueueService getQueueService() {
        return mainCtx.getQueueService();
    }

    @Override
    public EventLoopGroup getSharedEventLoop() {
        return mainCtx.getSharedEventLoopGroupService().getSharedEventLoopGroup();
    }

    @Override
    public MailService getMailService(boolean isSystem) {
        if (!isSystem || mainCtx.isAllowSystemMailService()) {
            return mainCtx.getMailService();
        } else {
            throw new RuntimeException("Access to System Mail Service is forbidden!");
        }
    }

    @Override
    public SmsService getSmsService() {
        if (mainCtx.isAllowSystemSmsService()) {
            return mainCtx.getSmsService();
        } else {
            throw new RuntimeException("Access to System SMS Service is forbidden!");
        }
    }

    @Override
    public SmsSenderFactory getSmsSenderFactory() {
        return mainCtx.getSmsSenderFactory();
    }

    @Override
    public RuleEngineRpcService getRpcService() {
        return mainCtx.getTbRuleEngineDeviceRpcService();
    }

    @Override
    public CassandraCluster getCassandraCluster() {
        return mainCtx.getCassandraCluster();
    }

    @Override
    public TbResultSetFuture submitCassandraReadTask(CassandraStatementTask task) {
        return mainCtx.getCassandraBufferedRateReadExecutor().submit(task);
    }

    @Override
    public TbResultSetFuture submitCassandraWriteTask(CassandraStatementTask task) {
        return mainCtx.getCassandraBufferedRateWriteExecutor().submit(task);
    }

    @Override
    public PageData<RuleNodeState> findRuleNodeStates(PageLink pageLink) {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}] Fetch Rule Node States.", getTenantId(), getSelfId());
        }
        return mainCtx.getRuleNodeStateService().findByRuleNodeId(getTenantId(), getSelfId(), pageLink);
    }

    @Override
    public RuleNodeState findRuleNodeStateForEntity(EntityId entityId) {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}][{}] Fetch Rule Node State for entity.", getTenantId(), getSelfId(), entityId);
        }
        return mainCtx.getRuleNodeStateService().findByRuleNodeIdAndEntityId(getTenantId(), getSelfId(), entityId);
    }

    @Override
    public RuleNodeState saveRuleNodeState(RuleNodeState state) {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}][{}] Persist Rule Node State for entity: {}", getTenantId(), getSelfId(), state.getEntityId(), state.getStateData());
        }
        state.setRuleNodeId(getSelfId());
        return mainCtx.getRuleNodeStateService().save(getTenantId(), state);
    }

    @Override
    public void clearRuleNodeStates() {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}] Going to clear rule node states", getTenantId(), getSelfId());
        }
        mainCtx.getRuleNodeStateService().removeByRuleNodeId(getTenantId(), getSelfId());
    }

    @Override
    public void removeRuleNodeStateForEntity(EntityId entityId) {
        if (log.isDebugEnabled()) {
            log.debug("[{}][{}][{}] Remove Rule Node State for entity.", getTenantId(), getSelfId(), entityId);
        }
        mainCtx.getRuleNodeStateService().removeByRuleNodeIdAndEntityId(getTenantId(), getSelfId(), entityId);
    }

    @Override
    public void addTenantProfileListener(Consumer<TenantProfile> listener) {
        mainCtx.getTenantProfileCache().addListener(getTenantId(), getSelfId(), listener);
    }

    @Override
    public void addDeviceProfileListeners(Consumer<DeviceProfile> profileListener, BiConsumer<DeviceId, DeviceProfile> deviceListener) {
        mainCtx.getDeviceProfileCache().addListener(getTenantId(), getSelfId(), profileListener, deviceListener);
    }

    @Override
    public void removeListeners() {
        mainCtx.getDeviceProfileCache().removeListener(getTenantId(), getSelfId());
        mainCtx.getTenantProfileCache().removeListener(getTenantId(), getSelfId());
    }

    @Override
    public TenantProfile getTenantProfile() {
        return mainCtx.getTenantProfileCache().get(getTenantId());
    }

    private TbMsgMetaData getActionMetaData(RuleNodeId ruleNodeId) {
        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue("ruleNodeId", ruleNodeId.toString());
        return metaData;
    }

    private class SimpleTbQueueCallback implements TbQueueCallback {
        private final Runnable onSuccess;
        private final Consumer<Throwable> onFailure;

        public SimpleTbQueueCallback(Runnable onSuccess, Consumer<Throwable> onFailure) {
            this.onSuccess = onSuccess;
            this.onFailure = onFailure;
        }

        @Override
        public void onSuccess(TbQueueMsgMetadata metadata) {
            if (onSuccess != null) {
                onSuccess.run();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            if (onFailure != null) {
                onFailure.accept(t);
            } else {
                log.debug("[{}] Failed to put item into queue", nodeCtx.getTenantId(), t);
            }
        }
    }
}
