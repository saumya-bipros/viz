package com.vizzionnaire.server.common.transport.service;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import com.vizzionnaire.common.util.VizzionnaireExecutors;
import com.vizzionnaire.common.util.VizzionnaireThreadFactory;
import com.vizzionnaire.server.common.data.ApiUsageRecordKey;
import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.DeviceTransportType;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.ResourceType;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.device.data.PowerMode;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.TenantProfileId;
import com.vizzionnaire.server.common.data.rpc.RpcStatus;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;
import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.common.msg.queue.TopicPartitionInfo;
import com.vizzionnaire.server.common.msg.session.SessionMsgType;
import com.vizzionnaire.server.common.msg.tools.TbRateLimitsException;
import com.vizzionnaire.server.common.stats.MessagesStats;
import com.vizzionnaire.server.common.stats.StatsFactory;
import com.vizzionnaire.server.common.stats.StatsType;
import com.vizzionnaire.server.common.transport.DeviceDeletedEvent;
import com.vizzionnaire.server.common.transport.DeviceProfileUpdatedEvent;
import com.vizzionnaire.server.common.transport.DeviceUpdatedEvent;
import com.vizzionnaire.server.common.transport.SessionMsgListener;
import com.vizzionnaire.server.common.transport.TransportDeviceProfileCache;
import com.vizzionnaire.server.common.transport.TransportResourceCache;
import com.vizzionnaire.server.common.transport.TransportService;
import com.vizzionnaire.server.common.transport.TransportServiceCallback;
import com.vizzionnaire.server.common.transport.TransportTenantProfileCache;
import com.vizzionnaire.server.common.transport.auth.GetOrCreateDeviceFromGatewayResponse;
import com.vizzionnaire.server.common.transport.auth.TransportDeviceInfo;
import com.vizzionnaire.server.common.transport.auth.ValidateDeviceCredentialsResponse;
import com.vizzionnaire.server.common.transport.limits.TransportRateLimitService;
import com.vizzionnaire.server.common.transport.util.JsonUtils;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.gen.transport.TransportProtos.ProvisionDeviceRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ProvisionDeviceResponseMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToCoreMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToTransportMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiResponseMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportToDeviceActorMsg;
import com.vizzionnaire.server.queue.TbQueueCallback;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.TbQueueMsgMetadata;
import com.vizzionnaire.server.queue.TbQueueProducer;
import com.vizzionnaire.server.queue.TbQueueRequestTemplate;
import com.vizzionnaire.server.queue.common.AsyncCallbackTemplate;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.discovery.NotificationsTopicService;
import com.vizzionnaire.server.queue.discovery.PartitionService;
import com.vizzionnaire.server.queue.discovery.TbServiceInfoProvider;
import com.vizzionnaire.server.queue.provider.TbQueueProducerProvider;
import com.vizzionnaire.server.queue.provider.TbTransportQueueFactory;
import com.vizzionnaire.server.queue.scheduler.SchedulerComponent;
import com.vizzionnaire.server.queue.usagestats.TbApiUsageClient;
import com.vizzionnaire.server.queue.util.AfterStartUp;
import com.vizzionnaire.server.queue.util.DataDecodingEncodingService;
import com.vizzionnaire.server.queue.util.TbTransportComponent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by ashvayka on 17.10.18.
 */
@Slf4j
@Service
@TbTransportComponent
public class DefaultTransportService implements TransportService {

    public static final String OVERWRITE_ACTIVITY_TIME = "overwriteActivityTime";
    public static final String SESSION_EXPIRED_MESSAGE = "Session has expired due to last activity time!";
    public static final TransportProtos.SessionEventMsg SESSION_EVENT_MSG_OPEN = getSessionEventMsg(TransportProtos.SessionEvent.OPEN);
    public static final TransportProtos.SessionEventMsg SESSION_EVENT_MSG_CLOSED = getSessionEventMsg(TransportProtos.SessionEvent.CLOSED);
    public static final TransportProtos.SessionCloseNotificationProto SESSION_CLOSE_NOTIFICATION_PROTO = TransportProtos.SessionCloseNotificationProto.newBuilder()
            .setMessage(SESSION_EXPIRED_MESSAGE).build();
    public static final TransportProtos.SubscribeToAttributeUpdatesMsg SUBSCRIBE_TO_ATTRIBUTE_UPDATES_ASYNC_MSG = TransportProtos.SubscribeToAttributeUpdatesMsg.newBuilder()
            .setSessionType(TransportProtos.SessionType.ASYNC).build();
    public static final TransportProtos.SubscribeToRPCMsg SUBSCRIBE_TO_RPC_ASYNC_MSG = TransportProtos.SubscribeToRPCMsg.newBuilder()
            .setSessionType(TransportProtos.SessionType.ASYNC).build();

    private final AtomicInteger atomicTs = new AtomicInteger(0);

    @Value("${transport.log.enabled:true}")
    private boolean logEnabled;
    @Value("${transport.log.max_length:1024}")
    private int logMaxLength;
    @Value("${transport.sessions.inactivity_timeout}")
    private long sessionInactivityTimeout;
    @Value("${transport.sessions.report_timeout}")
    private long sessionReportTimeout;
    @Value("${transport.client_side_rpc.timeout:60000}")
    private long clientSideRpcTimeout;
    @Value("${queue.transport.poll_interval}")
    private int notificationsPollDuration;
    @Value("${transport.stats.enabled:false}")
    private boolean statsEnabled;

    @Autowired
    @Lazy
    private TbApiUsageClient apiUsageClient;
    private final Map<String, Number> statsMap = new LinkedHashMap<>();

    private final Gson gson = new Gson();
    private final PartitionService partitionService;
    private final TbTransportQueueFactory queueProvider;
    private final TbQueueProducerProvider producerProvider;

    private final NotificationsTopicService notificationsTopicService;
    private final TbServiceInfoProvider serviceInfoProvider;
    private final StatsFactory statsFactory;
    private final TransportDeviceProfileCache deviceProfileCache;
    private final TransportTenantProfileCache tenantProfileCache;

    private final TransportRateLimitService rateLimitService;
    private final DataDecodingEncodingService dataDecodingEncodingService;
    private final SchedulerComponent scheduler;
    private final ApplicationEventPublisher eventPublisher;
    private final TransportResourceCache transportResourceCache;

    protected TbQueueRequestTemplate<TbProtoQueueMsg<TransportApiRequestMsg>, TbProtoQueueMsg<TransportApiResponseMsg>> transportApiRequestTemplate;
    protected TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> ruleEngineMsgProducer;
    protected TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> tbCoreMsgProducer;
    protected TbQueueConsumer<TbProtoQueueMsg<ToTransportMsg>> transportNotificationsConsumer;

    protected MessagesStats ruleEngineProducerStats;
    protected MessagesStats tbCoreProducerStats;
    protected MessagesStats transportApiStats;

    protected ExecutorService transportCallbackExecutor;
    private ExecutorService mainConsumerExecutor;

    public final ConcurrentMap<UUID, SessionMetaData> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, SessionActivityData> sessionsActivity = new ConcurrentHashMap<>();
    private final Map<String, RpcRequestMetadata> toServerRpcPendingMap = new ConcurrentHashMap<>();

    private volatile boolean stopped = false;

    public DefaultTransportService(PartitionService partitionService,
                                   TbServiceInfoProvider serviceInfoProvider,
                                   TbTransportQueueFactory queueProvider,
                                   TbQueueProducerProvider producerProvider,
                                   NotificationsTopicService notificationsTopicService,
                                   StatsFactory statsFactory,
                                   TransportDeviceProfileCache deviceProfileCache,
                                   TransportTenantProfileCache tenantProfileCache,
                                   TransportRateLimitService rateLimitService,
                                   DataDecodingEncodingService dataDecodingEncodingService, SchedulerComponent scheduler, TransportResourceCache transportResourceCache,
                                   ApplicationEventPublisher eventPublisher) {
        this.partitionService = partitionService;
        this.serviceInfoProvider = serviceInfoProvider;
        this.queueProvider = queueProvider;
        this.producerProvider = producerProvider;
        this.notificationsTopicService = notificationsTopicService;
        this.statsFactory = statsFactory;
        this.deviceProfileCache = deviceProfileCache;
        this.tenantProfileCache = tenantProfileCache;
        this.rateLimitService = rateLimitService;
        this.dataDecodingEncodingService = dataDecodingEncodingService;
        this.scheduler = scheduler;
        this.transportResourceCache = transportResourceCache;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        this.ruleEngineProducerStats = statsFactory.createMessagesStats(StatsType.RULE_ENGINE.getName() + ".producer");
        this.tbCoreProducerStats = statsFactory.createMessagesStats(StatsType.CORE.getName() + ".producer");
        this.transportApiStats = statsFactory.createMessagesStats(StatsType.TRANSPORT.getName() + ".producer");
        this.transportCallbackExecutor = VizzionnaireExecutors.newWorkStealingPool(20, getClass());
        this.scheduler.scheduleAtFixedRate(this::checkInactivityAndReportActivity, new Random().nextInt((int) sessionReportTimeout), sessionReportTimeout, TimeUnit.MILLISECONDS);
        this.scheduler.scheduleAtFixedRate(this::invalidateRateLimits, new Random().nextInt((int) sessionReportTimeout), sessionReportTimeout, TimeUnit.MILLISECONDS);
        transportApiRequestTemplate = queueProvider.createTransportApiRequestTemplate();
        transportApiRequestTemplate.setMessagesStats(transportApiStats);
        ruleEngineMsgProducer = producerProvider.getRuleEngineMsgProducer();
        tbCoreMsgProducer = producerProvider.getTbCoreMsgProducer();
        transportNotificationsConsumer = queueProvider.createTransportNotificationsConsumer();
        TopicPartitionInfo tpi = notificationsTopicService.getNotificationsTopic(ServiceType.TB_TRANSPORT, serviceInfoProvider.getServiceId());
        transportNotificationsConsumer.subscribe(Collections.singleton(tpi));
        transportApiRequestTemplate.init();
        mainConsumerExecutor = Executors.newSingleThreadExecutor(VizzionnaireThreadFactory.forName("transport-consumer"));
    }

    @AfterStartUp(order = AfterStartUp.TRANSPORT_SERVICE)
    private void start() {
        mainConsumerExecutor.execute(() -> {
            while (!stopped) {
                try {
                    List<TbProtoQueueMsg<ToTransportMsg>> records = transportNotificationsConsumer.poll(notificationsPollDuration);
                    if (records.size() == 0) {
                        continue;
                    }
                    records.forEach(record -> {
                        try {
                            processToTransportMsg(record.getValue());
                        } catch (Throwable e) {
                            log.warn("Failed to process the notification.", e);
                        }
                    });
                    transportNotificationsConsumer.commit();
                } catch (Exception e) {
                    if (!stopped) {
                        log.warn("Failed to obtain messages from queue.", e);
                        try {
                            Thread.sleep(notificationsPollDuration);
                        } catch (InterruptedException e2) {
                            log.trace("Failed to wait until the server has capacity to handle new requests", e2);
                        }
                    }
                }
            }
        });
    }

    private void invalidateRateLimits() {
        rateLimitService.invalidateRateLimitsIpTable(sessionInactivityTimeout);
    }

    @PreDestroy
    public void destroy() {
        stopped = true;

        if (transportNotificationsConsumer != null) {
            transportNotificationsConsumer.unsubscribe();
        }
        if (transportCallbackExecutor != null) {
            transportCallbackExecutor.shutdownNow();
        }
        if (mainConsumerExecutor != null) {
            mainConsumerExecutor.shutdownNow();
        }
        if (transportApiRequestTemplate != null) {
            transportApiRequestTemplate.stop();
        }
    }

    @Override
    public SessionMetaData registerAsyncSession(TransportProtos.SessionInfoProto sessionInfo, SessionMsgListener listener) {
        return sessions.computeIfAbsent(toSessionId(sessionInfo), (x) -> new SessionMetaData(sessionInfo, TransportProtos.SessionType.ASYNC, listener));
    }

    @Override
    public TransportProtos.GetEntityProfileResponseMsg getEntityProfile(TransportProtos.GetEntityProfileRequestMsg msg) {
        TbProtoQueueMsg<TransportProtos.TransportApiRequestMsg> protoMsg =
                new TbProtoQueueMsg<>(UUID.randomUUID(), TransportProtos.TransportApiRequestMsg.newBuilder().setEntityProfileRequestMsg(msg).build());
        try {
            TbProtoQueueMsg<TransportApiResponseMsg> response = transportApiRequestTemplate.send(protoMsg).get();
            return response.getValue().getEntityProfileResponseMsg();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TransportProtos.GetQueueRoutingInfoResponseMsg> getQueueRoutingInfo(TransportProtos.GetAllQueueRoutingInfoRequestMsg msg) {
        TbProtoQueueMsg<TransportProtos.TransportApiRequestMsg> protoMsg =
                new TbProtoQueueMsg<>(UUID.randomUUID(), TransportProtos.TransportApiRequestMsg.newBuilder().setGetAllQueueRoutingInfoRequestMsg(msg).build());
        try {
            TbProtoQueueMsg<TransportApiResponseMsg> response = transportApiRequestTemplate.send(protoMsg).get();
            return response.getValue().getGetQueueRoutingInfoResponseMsgsList();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TransportProtos.GetResourceResponseMsg getResource(TransportProtos.GetResourceRequestMsg msg) {
        TbProtoQueueMsg<TransportProtos.TransportApiRequestMsg> protoMsg =
                new TbProtoQueueMsg<>(UUID.randomUUID(), TransportProtos.TransportApiRequestMsg.newBuilder().setResourceRequestMsg(msg).build());
        try {
            TbProtoQueueMsg<TransportApiResponseMsg> response = transportApiRequestTemplate.send(protoMsg).get();
            return response.getValue().getResourceResponseMsg();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TransportProtos.GetSnmpDevicesResponseMsg getSnmpDevicesIds(TransportProtos.GetSnmpDevicesRequestMsg requestMsg) {
        TbProtoQueueMsg<TransportProtos.TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(
                UUID.randomUUID(), TransportProtos.TransportApiRequestMsg.newBuilder()
                .setSnmpDevicesRequestMsg(requestMsg)
                .build()
        );

        try {
            TbProtoQueueMsg<TransportApiResponseMsg> response = transportApiRequestTemplate.send(protoMsg).get();
            return response.getValue().getSnmpDevicesResponseMsg();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TransportProtos.GetDeviceResponseMsg getDevice(TransportProtos.GetDeviceRequestMsg requestMsg) {
        TbProtoQueueMsg<TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(
                UUID.randomUUID(), TransportProtos.TransportApiRequestMsg.newBuilder()
                .setDeviceRequestMsg(requestMsg)
                .build()
        );

        try {
            TransportApiResponseMsg response = transportApiRequestTemplate.send(protoMsg).get().getValue();
            if (response.hasDeviceResponseMsg()) {
                return response.getDeviceResponseMsg();
            } else {
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TransportProtos.GetDeviceCredentialsResponseMsg getDeviceCredentials(TransportProtos.GetDeviceCredentialsRequestMsg requestMsg) {
        TbProtoQueueMsg<TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(
                UUID.randomUUID(), TransportProtos.TransportApiRequestMsg.newBuilder()
                .setDeviceCredentialsRequestMsg(requestMsg)
                .build()
        );

        try {
            TbProtoQueueMsg<TransportApiResponseMsg> response = transportApiRequestTemplate.send(protoMsg).get();
            return response.getValue().getDeviceCredentialsResponseMsg();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(DeviceTransportType transportType, TransportProtos.ValidateDeviceTokenRequestMsg msg,
                        TransportServiceCallback<ValidateDeviceCredentialsResponse> callback) {
        log.trace("Processing msg: {}", msg);
        TbProtoQueueMsg<TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(UUID.randomUUID(),
                TransportApiRequestMsg.newBuilder().setValidateTokenRequestMsg(msg).build());
        doProcess(transportType, protoMsg, callback);
    }

    @Override
    public void process(DeviceTransportType transportType, TransportProtos.ValidateBasicMqttCredRequestMsg msg,
                        TransportServiceCallback<ValidateDeviceCredentialsResponse> callback) {
        log.trace("Processing msg: {}", msg);
        TbProtoQueueMsg<TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(UUID.randomUUID(),
                TransportApiRequestMsg.newBuilder().setValidateBasicMqttCredRequestMsg(msg).build());
        doProcess(transportType, protoMsg, callback);
    }

    @Override
    public void process(TransportProtos.ValidateDeviceLwM2MCredentialsRequestMsg requestMsg, TransportServiceCallback<ValidateDeviceCredentialsResponse> callback) {
        log.trace("Processing msg: {}", requestMsg);
        TbProtoQueueMsg<TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(UUID.randomUUID(), TransportApiRequestMsg.newBuilder().setValidateDeviceLwM2MCredentialsRequestMsg(requestMsg).build());
        ListenableFuture<ValidateDeviceCredentialsResponse> response = Futures.transform(transportApiRequestTemplate.send(protoMsg), tmp -> {
            TransportProtos.ValidateDeviceCredentialsResponseMsg msg = tmp.getValue().getValidateCredResponseMsg();
            ValidateDeviceCredentialsResponse.ValidateDeviceCredentialsResponseBuilder result = ValidateDeviceCredentialsResponse.builder();
            if (msg.hasDeviceInfo()) {
                result.credentials(msg.getCredentialsBody());
                TransportDeviceInfo tdi = getTransportDeviceInfo(msg.getDeviceInfo());
                result.deviceInfo(tdi);
                ByteString profileBody = msg.getProfileBody();
                if (!profileBody.isEmpty()) {
                    DeviceProfile profile = deviceProfileCache.getOrCreate(tdi.getDeviceProfileId(), profileBody);
                    result.deviceProfile(profile);
                }
            }
            return result.build();
        }, MoreExecutors.directExecutor());
        AsyncCallbackTemplate.withCallback(response, callback::onSuccess, callback::onError, transportCallbackExecutor);
    }

    @Override
    public void process(DeviceTransportType transportType, TransportProtos.ValidateDeviceX509CertRequestMsg msg, TransportServiceCallback<ValidateDeviceCredentialsResponse> callback) {
        log.trace("Processing msg: {}", msg);
        TbProtoQueueMsg<TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(UUID.randomUUID(), TransportApiRequestMsg.newBuilder().setValidateX509CertRequestMsg(msg).build());
        doProcess(transportType, protoMsg, callback);
    }

    private void doProcess(DeviceTransportType transportType, TbProtoQueueMsg<TransportApiRequestMsg> protoMsg,
                           TransportServiceCallback<ValidateDeviceCredentialsResponse> callback) {
        ListenableFuture<ValidateDeviceCredentialsResponse> response = Futures.transform(transportApiRequestTemplate.send(protoMsg), tmp -> {
            TransportProtos.ValidateDeviceCredentialsResponseMsg msg = tmp.getValue().getValidateCredResponseMsg();
            ValidateDeviceCredentialsResponse.ValidateDeviceCredentialsResponseBuilder result = ValidateDeviceCredentialsResponse.builder();
            if (msg.hasDeviceInfo()) {
                result.credentials(msg.getCredentialsBody());
                TransportDeviceInfo tdi = getTransportDeviceInfo(msg.getDeviceInfo());
                result.deviceInfo(tdi);
                ByteString profileBody = msg.getProfileBody();
                if (!profileBody.isEmpty()) {
                    DeviceProfile profile = deviceProfileCache.getOrCreate(tdi.getDeviceProfileId(), profileBody);
                    if (transportType != DeviceTransportType.DEFAULT
                            && profile != null && profile.getTransportType() != DeviceTransportType.DEFAULT && profile.getTransportType() != transportType) {
                        log.debug("[{}] Device profile [{}] has different transport type: {}, expected: {}", tdi.getDeviceId(), tdi.getDeviceProfileId(), profile.getTransportType(), transportType);
                        throw new IllegalStateException("Device profile has different transport type: " + profile.getTransportType() + ". Expected: " + transportType);
                    }
                    result.deviceProfile(profile);
                }
            }
            return result.build();
        }, MoreExecutors.directExecutor());
        AsyncCallbackTemplate.withCallback(response, callback::onSuccess, callback::onError, transportCallbackExecutor);
    }

    @Override
    public void process(TransportProtos.GetOrCreateDeviceFromGatewayRequestMsg requestMsg, TransportServiceCallback<GetOrCreateDeviceFromGatewayResponse> callback) {
        TbProtoQueueMsg<TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(UUID.randomUUID(), TransportApiRequestMsg.newBuilder().setGetOrCreateDeviceRequestMsg(requestMsg).build());
        log.trace("Processing msg: {}", requestMsg);
        ListenableFuture<GetOrCreateDeviceFromGatewayResponse> response = Futures.transform(transportApiRequestTemplate.send(protoMsg), tmp -> {
            TransportProtos.GetOrCreateDeviceFromGatewayResponseMsg msg = tmp.getValue().getGetOrCreateDeviceResponseMsg();
            GetOrCreateDeviceFromGatewayResponse.GetOrCreateDeviceFromGatewayResponseBuilder result = GetOrCreateDeviceFromGatewayResponse.builder();
            if (msg.hasDeviceInfo()) {
                TransportDeviceInfo tdi = getTransportDeviceInfo(msg.getDeviceInfo());
                result.deviceInfo(tdi);
                ByteString profileBody = msg.getProfileBody();
                if (profileBody != null && !profileBody.isEmpty()) {
                    result.deviceProfile(deviceProfileCache.getOrCreate(tdi.getDeviceProfileId(), profileBody));
                }
            }
            return result.build();
        }, MoreExecutors.directExecutor());
        AsyncCallbackTemplate.withCallback(response, callback::onSuccess, callback::onError, transportCallbackExecutor);
    }

    @Override
    public void process(TransportProtos.LwM2MRequestMsg msg, TransportServiceCallback<TransportProtos.LwM2MResponseMsg> callback) {
        log.trace("Processing msg: {}", msg);
        TbProtoQueueMsg<TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(UUID.randomUUID(),
                TransportApiRequestMsg.newBuilder().setLwM2MRequestMsg(msg).build());
        AsyncCallbackTemplate.withCallback(transportApiRequestTemplate.send(protoMsg),
                response -> callback.onSuccess(response.getValue().getLwM2MResponseMsg()), callback::onError, transportCallbackExecutor);
    }

    private TransportDeviceInfo getTransportDeviceInfo(TransportProtos.DeviceInfoProto di) {
        TransportDeviceInfo tdi = new TransportDeviceInfo();
        tdi.setTenantId(TenantId.fromUUID(new UUID(di.getTenantIdMSB(), di.getTenantIdLSB())));
        tdi.setCustomerId(new CustomerId(new UUID(di.getCustomerIdMSB(), di.getCustomerIdLSB())));
        tdi.setDeviceId(new DeviceId(new UUID(di.getDeviceIdMSB(), di.getDeviceIdLSB())));
        tdi.setDeviceProfileId(new DeviceProfileId(new UUID(di.getDeviceProfileIdMSB(), di.getDeviceProfileIdLSB())));
        tdi.setAdditionalInfo(di.getAdditionalInfo());
        tdi.setDeviceName(di.getDeviceName());
        tdi.setDeviceType(di.getDeviceType());
        if (StringUtils.isNotEmpty(di.getPowerMode())) {
            tdi.setPowerMode(PowerMode.valueOf(di.getPowerMode()));
            tdi.setEdrxCycle(di.getEdrxCycle());
            tdi.setPsmActivityTimer(di.getPsmActivityTimer());
            tdi.setPagingTransmissionWindow(di.getPagingTransmissionWindow());
        }
        return tdi;
    }

    @Override
    public void process(ProvisionDeviceRequestMsg requestMsg, TransportServiceCallback<ProvisionDeviceResponseMsg> callback) {
        log.trace("Processing msg: {}", requestMsg);
        TbProtoQueueMsg<TransportApiRequestMsg> protoMsg = new TbProtoQueueMsg<>(UUID.randomUUID(), TransportApiRequestMsg.newBuilder().setProvisionDeviceRequestMsg(requestMsg).build());
        ListenableFuture<ProvisionDeviceResponseMsg> response = Futures.transform(transportApiRequestTemplate.send(protoMsg), tmp ->
                        tmp.getValue().getProvisionDeviceResponseMsg()
                , MoreExecutors.directExecutor());
        AsyncCallbackTemplate.withCallback(response, callback::onSuccess, callback::onError, transportCallbackExecutor);
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.SubscriptionInfoProto msg, TransportServiceCallback<Void> callback) {
        if (log.isTraceEnabled()) {
            log.trace("[{}] Processing msg: {}", toSessionId(sessionInfo), msg);
        }
        sendToDeviceActor(sessionInfo, TransportToDeviceActorMsg.newBuilder().setSessionInfo(sessionInfo)
                .setSubscriptionInfo(msg).build(), callback);
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.SessionEventMsg msg, TransportServiceCallback<Void> callback) {
        if (checkLimits(sessionInfo, msg, callback)) {
            reportActivityInternal(sessionInfo);
            sendToDeviceActor(sessionInfo, TransportToDeviceActorMsg.newBuilder().setSessionInfo(sessionInfo)
                    .setSessionEvent(msg).build(), callback);
        }
    }

    @Override
    public void process(TransportToDeviceActorMsg msg, TransportServiceCallback<Void> callback) {
        TransportProtos.SessionInfoProto sessionInfo = msg.getSessionInfo();
        if (checkLimits(sessionInfo, msg, callback)) {
            SessionMetaData sessionMetaData = sessions.get(toSessionId(sessionInfo));
            if (sessionMetaData != null) {
                if (msg.hasSubscribeToAttributes()) {
                    sessionMetaData.setSubscribedToAttributes(true);
                }
                if (msg.hasSubscribeToRPC()) {
                    sessionMetaData.setSubscribedToRPC(true);
                }
            }

            reportActivityInternal(sessionInfo);
            sendToDeviceActor(sessionInfo, msg, callback);
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.PostTelemetryMsg msg, TransportServiceCallback<Void> callback) {
        int dataPoints = 0;
        for (TransportProtos.TsKvListProto tsKv : msg.getTsKvListList()) {
            dataPoints += tsKv.getKvCount();
        }
        if (checkLimits(sessionInfo, msg, callback, dataPoints)) {
            reportActivityInternal(sessionInfo);
            TenantId tenantId = getTenantId(sessionInfo);
            DeviceId deviceId = new DeviceId(new UUID(sessionInfo.getDeviceIdMSB(), sessionInfo.getDeviceIdLSB()));
            CustomerId customerId = getCustomerId(sessionInfo);
            MsgPackCallback packCallback = new MsgPackCallback(msg.getTsKvListCount(), new ApiStatsProxyCallback<>(tenantId, customerId, dataPoints, callback));
            for (TransportProtos.TsKvListProto tsKv : msg.getTsKvListList()) {
                TbMsgMetaData metaData = new TbMsgMetaData();
                metaData.putValue("deviceName", sessionInfo.getDeviceName());
                metaData.putValue("deviceType", sessionInfo.getDeviceType());
                metaData.putValue("ts", tsKv.getTs() + "");
                JsonObject json = JsonUtils.getJsonObject(tsKv.getKvList());
                sendToRuleEngine(tenantId, deviceId, customerId, sessionInfo, json, metaData, SessionMsgType.POST_TELEMETRY_REQUEST, packCallback);
            }
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.PostAttributeMsg msg, TransportServiceCallback<Void> callback) {
        if (checkLimits(sessionInfo, msg, callback, msg.getKvCount())) {
            reportActivityInternal(sessionInfo);
            TenantId tenantId = getTenantId(sessionInfo);
            DeviceId deviceId = new DeviceId(new UUID(sessionInfo.getDeviceIdMSB(), sessionInfo.getDeviceIdLSB()));
            JsonObject json = JsonUtils.getJsonObject(msg.getKvList());
            TbMsgMetaData metaData = new TbMsgMetaData();
            metaData.putValue("deviceName", sessionInfo.getDeviceName());
            metaData.putValue("deviceType", sessionInfo.getDeviceType());
            metaData.putValue("notifyDevice", "false");
            CustomerId customerId = getCustomerId(sessionInfo);
            sendToRuleEngine(tenantId, deviceId, customerId, sessionInfo, json, metaData, SessionMsgType.POST_ATTRIBUTES_REQUEST,
                    new TransportTbQueueCallback(new ApiStatsProxyCallback<>(tenantId, customerId, msg.getKvList().size(), callback)));
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.GetAttributeRequestMsg msg, TransportServiceCallback<Void> callback) {
        if (checkLimits(sessionInfo, msg, callback)) {
            reportActivityInternal(sessionInfo);
            sendToDeviceActor(sessionInfo, TransportToDeviceActorMsg.newBuilder().setSessionInfo(sessionInfo)
                    .setGetAttributes(msg).build(), new ApiStatsProxyCallback<>(getTenantId(sessionInfo), getCustomerId(sessionInfo), 1, callback));
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.SubscribeToAttributeUpdatesMsg msg, TransportServiceCallback<Void> callback) {
        if (checkLimits(sessionInfo, msg, callback)) {
            SessionMetaData sessionMetaData = sessions.get(toSessionId(sessionInfo));
            if (sessionMetaData != null) {
                sessionMetaData.setSubscribedToAttributes(!msg.getUnsubscribe());
            }
            reportActivityInternal(sessionInfo);
            sendToDeviceActor(sessionInfo, TransportToDeviceActorMsg.newBuilder().setSessionInfo(sessionInfo).setSubscribeToAttributes(msg).build(),
                    new ApiStatsProxyCallback<>(getTenantId(sessionInfo), getCustomerId(sessionInfo), 1, callback));
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.SubscribeToRPCMsg msg, TransportServiceCallback<Void> callback) {
        if (checkLimits(sessionInfo, msg, callback)) {
            SessionMetaData sessionMetaData = sessions.get(toSessionId(sessionInfo));
            if (sessionMetaData != null) {
                sessionMetaData.setSubscribedToRPC(!msg.getUnsubscribe());
            }
            reportActivityInternal(sessionInfo);
            sendToDeviceActor(sessionInfo, TransportToDeviceActorMsg.newBuilder().setSessionInfo(sessionInfo).setSubscribeToRPC(msg).build(),
                    new ApiStatsProxyCallback<>(getTenantId(sessionInfo), getCustomerId(sessionInfo), 1, callback));
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.ToDeviceRpcResponseMsg msg, TransportServiceCallback<Void> callback) {
        if (checkLimits(sessionInfo, msg, callback)) {
            reportActivityInternal(sessionInfo);
            sendToDeviceActor(sessionInfo, TransportToDeviceActorMsg.newBuilder().setSessionInfo(sessionInfo).setToDeviceRPCCallResponse(msg).build(),
                    new ApiStatsProxyCallback<>(getTenantId(sessionInfo), getCustomerId(sessionInfo), 1, callback));
        }
    }

    @Override
    public void notifyAboutUplink(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.UplinkNotificationMsg msg, TransportServiceCallback<Void> callback) {
        if (checkLimits(sessionInfo, msg, callback)) {
            reportActivityInternal(sessionInfo);
            sendToDeviceActor(sessionInfo, TransportToDeviceActorMsg.newBuilder().setSessionInfo(sessionInfo).setUplinkNotificationMsg(msg).build(), callback);
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.ToDeviceRpcRequestMsg msg, RpcStatus rpcStatus, TransportServiceCallback<Void> callback) {
        TransportProtos.ToDeviceRpcResponseStatusMsg responseMsg = TransportProtos.ToDeviceRpcResponseStatusMsg.newBuilder()
                .setRequestId(msg.getRequestId())
                .setRequestIdLSB(msg.getRequestIdLSB())
                .setRequestIdMSB(msg.getRequestIdMSB())
                .setStatus(rpcStatus.name())
                .build();

        if (checkLimits(sessionInfo, responseMsg, callback)) {
            reportActivityInternal(sessionInfo);
            sendToDeviceActor(sessionInfo, TransportToDeviceActorMsg.newBuilder().setSessionInfo(sessionInfo).setRpcResponseStatusMsg(responseMsg).build(),
                    new ApiStatsProxyCallback<>(getTenantId(sessionInfo), getCustomerId(sessionInfo), 1, TransportServiceCallback.EMPTY));
        }
    }

    private void processTimeout(String requestId) {
        RpcRequestMetadata data = toServerRpcPendingMap.remove(requestId);
        if (data != null) {
            SessionMetaData md = sessions.get(data.getSessionId());
            if (md != null) {
                SessionMsgListener listener = md.getListener();
                transportCallbackExecutor.submit(() -> {
                    TransportProtos.ToServerRpcResponseMsg responseMsg =
                            TransportProtos.ToServerRpcResponseMsg.newBuilder()
                                    .setRequestId(data.getRequestId())
                                    .setError("timeout").build();
                    listener.onToServerRpcResponse(responseMsg);
                });
                if (md.getSessionType() == TransportProtos.SessionType.SYNC) {
                    deregisterSession(md.getSessionInfo());
                }
            } else {
                log.debug("[{}] Missing session.", data.getSessionId());
            }
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.ToServerRpcRequestMsg msg, TransportServiceCallback<Void> callback) {
        if (checkLimits(sessionInfo, msg, callback)) {
            reportActivityInternal(sessionInfo);
            UUID sessionId = toSessionId(sessionInfo);
            TenantId tenantId = getTenantId(sessionInfo);
            DeviceId deviceId = getDeviceId(sessionInfo);
            JsonObject json = new JsonObject();
            json.addProperty("method", msg.getMethodName());
            json.add("params", JsonUtils.parse(msg.getParams()));

            TbMsgMetaData metaData = new TbMsgMetaData();
            metaData.putValue("deviceName", sessionInfo.getDeviceName());
            metaData.putValue("deviceType", sessionInfo.getDeviceType());
            metaData.putValue("requestId", Integer.toString(msg.getRequestId()));
            metaData.putValue("serviceId", serviceInfoProvider.getServiceId());
            metaData.putValue("sessionId", sessionId.toString());
            sendToRuleEngine(tenantId, deviceId, getCustomerId(sessionInfo), sessionInfo, json, metaData,
                    SessionMsgType.TO_SERVER_RPC_REQUEST, new TransportTbQueueCallback(callback));
            String requestId = sessionId + "-" + msg.getRequestId();
            toServerRpcPendingMap.put(requestId, new RpcRequestMetadata(sessionId, msg.getRequestId()));
            scheduler.schedule(() -> processTimeout(requestId), clientSideRpcTimeout, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.ClaimDeviceMsg msg, TransportServiceCallback<Void> callback) {
        if (checkLimits(sessionInfo, msg, callback)) {
            reportActivityInternal(sessionInfo);
            sendToDeviceActor(sessionInfo, TransportToDeviceActorMsg.newBuilder().setSessionInfo(sessionInfo)
                    .setClaimDevice(msg).build(), callback);
        }
    }

    @Override
    public void process(TransportProtos.SessionInfoProto sessionInfo, TransportProtos.GetOtaPackageRequestMsg msg, TransportServiceCallback<TransportProtos.GetOtaPackageResponseMsg> callback) {
        if (checkLimits(sessionInfo, msg, callback)) {
            TbProtoQueueMsg<TransportProtos.TransportApiRequestMsg> protoMsg =
                    new TbProtoQueueMsg<>(UUID.randomUUID(), TransportProtos.TransportApiRequestMsg.newBuilder().setOtaPackageRequestMsg(msg).build());

            AsyncCallbackTemplate.withCallback(transportApiRequestTemplate.send(protoMsg), response -> {
                callback.onSuccess(response.getValue().getOtaPackageResponseMsg());
            }, callback::onError, transportCallbackExecutor);
        }
    }

    @Override
    public void reportActivity(TransportProtos.SessionInfoProto sessionInfo) {
        reportActivityInternal(sessionInfo);
    }

    private void reportActivityInternal(TransportProtos.SessionInfoProto sessionInfo) {
        UUID sessionId = toSessionId(sessionInfo);
        SessionActivityData sessionMetaData = sessionsActivity.computeIfAbsent(sessionId, id -> new SessionActivityData(sessionInfo));
        sessionMetaData.updateLastActivityTime();
    }

    private void checkInactivityAndReportActivity() {
        long expTime = System.currentTimeMillis() - sessionInactivityTimeout;
        Set<UUID> sessionsToRemove = new HashSet<>();
        sessionsActivity.forEach((uuid, sessionAD) -> {
            long lastActivityTime = sessionAD.getLastActivityTime();
            SessionMetaData sessionMD = sessions.get(uuid);
            if (sessionMD != null) {
                sessionAD.setSessionInfo(sessionMD.getSessionInfo());
            } else {
                sessionsToRemove.add(uuid);
            }
            TransportProtos.SessionInfoProto sessionInfo = sessionAD.getSessionInfo();

            if (sessionInfo.getGwSessionIdMSB() != 0 && sessionInfo.getGwSessionIdLSB() != 0) {
                var gwSessionId = new UUID(sessionInfo.getGwSessionIdMSB(), sessionInfo.getGwSessionIdLSB());
                SessionMetaData gwMetaData = sessions.get(gwSessionId);
                SessionActivityData gwActivityData = sessionsActivity.get(gwSessionId);
                if (gwMetaData != null && gwMetaData.isOverwriteActivityTime()) {
                    lastActivityTime = Math.max(gwActivityData.getLastActivityTime(), lastActivityTime);
                }
            }
            if (lastActivityTime < expTime) {
                if (sessionMD != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("[{}] Session has expired due to last activity time: {}", toSessionId(sessionInfo), lastActivityTime);
                    }
                    sessions.remove(uuid);
                    sessionsToRemove.add(uuid);
                    process(sessionInfo, SESSION_EVENT_MSG_CLOSED, null);
                    sessionMD.getListener().onRemoteSessionCloseCommand(uuid, SESSION_CLOSE_NOTIFICATION_PROTO);
                }
            } else {
                if (lastActivityTime > sessionAD.getLastReportedActivityTime()) {
                    final long lastActivityTimeFinal = lastActivityTime;
                    process(sessionInfo, TransportProtos.SubscriptionInfoProto.newBuilder()
                            .setAttributeSubscription(sessionMD != null && sessionMD.isSubscribedToAttributes())
                            .setRpcSubscription(sessionMD != null && sessionMD.isSubscribedToRPC())
                            .setLastActivityTime(lastActivityTime).build(), new TransportServiceCallback<Void>() {
                        @Override
                        public void onSuccess(Void msg) {
                            sessionAD.setLastReportedActivityTime(lastActivityTimeFinal);
                        }

                        @Override
                        public void onError(Throwable e) {
                            log.warn("[{}] Failed to report last activity time", uuid, e);
                        }
                    });
                }
            }
        });
        // Removes all closed or short-lived sessions.
        sessionsToRemove.forEach(sessionsActivity::remove);
    }

    @Override
    public SessionMetaData registerSyncSession(TransportProtos.SessionInfoProto sessionInfo, SessionMsgListener listener, long timeout) {
        SessionMetaData currentSession = new SessionMetaData(sessionInfo, TransportProtos.SessionType.SYNC, listener);
        UUID sessionId = toSessionId(sessionInfo);
        sessions.putIfAbsent(sessionId, currentSession);

        TransportProtos.SessionCloseNotificationProto notification = TransportProtos.SessionCloseNotificationProto.newBuilder().setMessage("session timeout!").build();

        ScheduledFuture executorFuture = scheduler.schedule(() -> {
            listener.onRemoteSessionCloseCommand(sessionId, notification);
            deregisterSession(sessionInfo);
        }, timeout, TimeUnit.MILLISECONDS);

        currentSession.setScheduledFuture(executorFuture);
        return currentSession;
    }

    @Override
    public void deregisterSession(TransportProtos.SessionInfoProto sessionInfo) {
        SessionMetaData currentSession = sessions.get(toSessionId(sessionInfo));
        if (currentSession != null && currentSession.hasScheduledFuture()) {
            log.debug("Stopping scheduler to avoid resending response if request has been ack.");
            currentSession.getScheduledFuture().cancel(false);
        }
        sessions.remove(toSessionId(sessionInfo));
    }

    @Override
    public void log(TransportProtos.SessionInfoProto sessionInfo, String msg) {
        if (!logEnabled || sessionInfo == null || StringUtils.isEmpty(msg)) {
            return;
        }
        if (msg.length() > logMaxLength) {
            msg = msg.substring(0, logMaxLength);
        }
        TransportProtos.PostTelemetryMsg.Builder request = TransportProtos.PostTelemetryMsg.newBuilder();
        TransportProtos.TsKvListProto.Builder builder = TransportProtos.TsKvListProto.newBuilder();
        builder.setTs(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) * 1000L + (atomicTs.getAndIncrement() % 1000));
        builder.addKv(TransportProtos.KeyValueProto.newBuilder()
                .setKey("transportLog")
                .setType(TransportProtos.KeyValueType.STRING_V)
                .setStringV(msg).build());
        request.addTsKvList(builder.build());
        TransportProtos.PostTelemetryMsg postTelemetryMsg = request.build();
        process(sessionInfo, postTelemetryMsg, TransportServiceCallback.EMPTY);
    }

    private boolean checkLimits(TransportProtos.SessionInfoProto sessionInfo, Object msg, TransportServiceCallback<?> callback) {
        return checkLimits(sessionInfo, msg, callback, 0);
    }

    private boolean checkLimits(TransportProtos.SessionInfoProto sessionInfo, Object msg, TransportServiceCallback<?> callback, int dataPoints) {
        if (log.isTraceEnabled()) {
            log.trace("[{}] Processing msg: {}", toSessionId(sessionInfo), msg);
        }
        TenantId tenantId = TenantId.fromUUID(new UUID(sessionInfo.getTenantIdMSB(), sessionInfo.getTenantIdLSB()));
        DeviceId deviceId = new DeviceId(new UUID(sessionInfo.getDeviceIdMSB(), sessionInfo.getDeviceIdLSB()));

        EntityType rateLimitedEntityType = rateLimitService.checkLimits(tenantId, deviceId, dataPoints);
        if (rateLimitedEntityType == null) {
            return true;
        } else {
            if (callback != null) {
                callback.onError(new TbRateLimitsException(rateLimitedEntityType));
            }
            return false;
        }
    }

    protected void processToTransportMsg(TransportProtos.ToTransportMsg toSessionMsg) {
        UUID sessionId = new UUID(toSessionMsg.getSessionIdMSB(), toSessionMsg.getSessionIdLSB());
        SessionMetaData md = sessions.get(sessionId);
        if (md != null) {
            log.trace("[{}] Processing notification: {}", sessionId, toSessionMsg);
            SessionMsgListener listener = md.getListener();
            transportCallbackExecutor.submit(() -> {
                if (toSessionMsg.hasGetAttributesResponse()) {
                    listener.onGetAttributesResponse(toSessionMsg.getGetAttributesResponse());
                }
                if (toSessionMsg.hasAttributeUpdateNotification()) {
                    listener.onAttributeUpdate(sessionId, toSessionMsg.getAttributeUpdateNotification());
                }
                if (toSessionMsg.hasSessionCloseNotification()) {
                    listener.onRemoteSessionCloseCommand(sessionId, toSessionMsg.getSessionCloseNotification());
                }
                if (toSessionMsg.hasToTransportUpdateCredentialsNotification()) {
                    listener.onToTransportUpdateCredentials(toSessionMsg.getToTransportUpdateCredentialsNotification());
                }
                if (toSessionMsg.hasToDeviceRequest()) {
                    listener.onToDeviceRpcRequest(sessionId, toSessionMsg.getToDeviceRequest());
                }
                if (toSessionMsg.hasToServerResponse()) {
                    String requestId = sessionId + "-" + toSessionMsg.getToServerResponse().getRequestId();
                    toServerRpcPendingMap.remove(requestId);
                    listener.onToServerRpcResponse(toSessionMsg.getToServerResponse());
                }
            });
            if (md.getSessionType() == TransportProtos.SessionType.SYNC) {
                deregisterSession(md.getSessionInfo());
            }
        } else {
            log.trace("Processing broadcast notification: {}", toSessionMsg);
            if (toSessionMsg.hasEntityUpdateMsg()) {
                TransportProtos.EntityUpdateMsg msg = toSessionMsg.getEntityUpdateMsg();
                EntityType entityType = EntityType.valueOf(msg.getEntityType());
                if (EntityType.DEVICE_PROFILE.equals(entityType)) {
                    DeviceProfile deviceProfile = deviceProfileCache.put(msg.getData());
                    if (deviceProfile != null) {
                        log.debug("On device profile update: {}", deviceProfile);
                        onProfileUpdate(deviceProfile);
                    }
                } else if (EntityType.TENANT_PROFILE.equals(entityType)) {
                    rateLimitService.update(tenantProfileCache.put(msg.getData()));
                } else if (EntityType.TENANT.equals(entityType)) {
                    Optional<Tenant> profileOpt = dataDecodingEncodingService.decode(msg.getData().toByteArray());
                    if (profileOpt.isPresent()) {
                        Tenant tenant = profileOpt.get();
                        partitionService.removeTenant(tenant.getId());
                        boolean updated = tenantProfileCache.put(tenant.getId(), tenant.getTenantProfileId());
                        if (updated) {
                            rateLimitService.update(tenant.getId());
                        }
                    }
                } else if (EntityType.API_USAGE_STATE.equals(entityType)) {
                    Optional<ApiUsageState> stateOpt = dataDecodingEncodingService.decode(msg.getData().toByteArray());
                    if (stateOpt.isPresent()) {
                        ApiUsageState apiUsageState = stateOpt.get();
                        rateLimitService.update(apiUsageState.getTenantId(), apiUsageState.isTransportEnabled());
                        //TODO: if transport is disabled, we should close all sessions and not to check credentials.
                    }
                } else if (EntityType.DEVICE.equals(entityType)) {
                    Optional<Device> deviceOpt = dataDecodingEncodingService.decode(msg.getData().toByteArray());
                    deviceOpt.ifPresent(this::onDeviceUpdate);
                }
            } else if (toSessionMsg.hasEntityDeleteMsg()) {
                TransportProtos.EntityDeleteMsg msg = toSessionMsg.getEntityDeleteMsg();
                EntityType entityType = EntityType.valueOf(msg.getEntityType());
                UUID entityUuid = new UUID(msg.getEntityIdMSB(), msg.getEntityIdLSB());
                if (EntityType.DEVICE_PROFILE.equals(entityType)) {
                    deviceProfileCache.evict(new DeviceProfileId(new UUID(msg.getEntityIdMSB(), msg.getEntityIdLSB())));
                } else if (EntityType.TENANT_PROFILE.equals(entityType)) {
                    tenantProfileCache.remove(new TenantProfileId(entityUuid));
                } else if (EntityType.TENANT.equals(entityType)) {
                    rateLimitService.remove(TenantId.fromUUID(entityUuid));
                } else if (EntityType.DEVICE.equals(entityType)) {
                    rateLimitService.remove(new DeviceId(entityUuid));
                    onDeviceDeleted(new DeviceId(entityUuid));
                }
            } else if (toSessionMsg.hasResourceUpdateMsg()) {
                TransportProtos.ResourceUpdateMsg msg = toSessionMsg.getResourceUpdateMsg();
                TenantId tenantId = TenantId.fromUUID(new UUID(msg.getTenantIdMSB(), msg.getTenantIdLSB()));
                ResourceType resourceType = ResourceType.valueOf(msg.getResourceType());
                String resourceId = msg.getResourceKey();
                transportResourceCache.update(tenantId, resourceType, resourceId);
                sessions.forEach((id, mdRez) -> {
                    log.trace("ResourceUpdate - [{}] [{}]", id, mdRez);
                    transportCallbackExecutor.submit(() -> mdRez.getListener().onResourceUpdate(msg));
                });

            } else if (toSessionMsg.hasResourceDeleteMsg()) {
                TransportProtos.ResourceDeleteMsg msg = toSessionMsg.getResourceDeleteMsg();
                TenantId tenantId = TenantId.fromUUID(new UUID(msg.getTenantIdMSB(), msg.getTenantIdLSB()));
                ResourceType resourceType = ResourceType.valueOf(msg.getResourceType());
                String resourceId = msg.getResourceKey();
                transportResourceCache.evict(tenantId, resourceType, resourceId);
                sessions.forEach((id, mdRez) -> {
                    log.warn("ResourceDelete - [{}] [{}]", id, mdRez);
                    transportCallbackExecutor.submit(() -> mdRez.getListener().onResourceDelete(msg));
                });
            } else if (toSessionMsg.hasQueueUpdateMsg()) {
                partitionService.updateQueue(toSessionMsg.getQueueUpdateMsg());
            } else if (toSessionMsg.hasQueueDeleteMsg()) {
                partitionService.removeQueue(toSessionMsg.getQueueDeleteMsg());
            } else {
                //TODO: should we notify the device actor about missed session?
                log.debug("[{}] Missing session.", sessionId);
            }
        }
    }


    public void onProfileUpdate(DeviceProfile deviceProfile) {
        long deviceProfileIdMSB = deviceProfile.getId().getId().getMostSignificantBits();
        long deviceProfileIdLSB = deviceProfile.getId().getId().getLeastSignificantBits();
        sessions.forEach((id, md) -> {
            //TODO: if transport types are different - we should close the session.
            if (md.getSessionInfo().getDeviceProfileIdMSB() == deviceProfileIdMSB
                    && md.getSessionInfo().getDeviceProfileIdLSB() == deviceProfileIdLSB) {
                TransportProtos.SessionInfoProto newSessionInfo = TransportProtos.SessionInfoProto.newBuilder()
                        .mergeFrom(md.getSessionInfo())
                        .setDeviceProfileIdMSB(deviceProfileIdMSB)
                        .setDeviceProfileIdLSB(deviceProfileIdLSB)
                        .setDeviceType(deviceProfile.getName())
                        .build();
                md.setSessionInfo(newSessionInfo);
                transportCallbackExecutor.submit(() -> md.getListener().onDeviceProfileUpdate(newSessionInfo, deviceProfile));
            }
        });

        eventPublisher.publishEvent(new DeviceProfileUpdatedEvent(deviceProfile));
    }

    private void onDeviceUpdate(Device device) {
        long deviceIdMSB = device.getId().getId().getMostSignificantBits();
        long deviceIdLSB = device.getId().getId().getLeastSignificantBits();
        long deviceProfileIdMSB = device.getDeviceProfileId().getId().getMostSignificantBits();
        long deviceProfileIdLSB = device.getDeviceProfileId().getId().getLeastSignificantBits();
        sessions.forEach((id, md) -> {
            if ((md.getSessionInfo().getDeviceIdMSB() == deviceIdMSB && md.getSessionInfo().getDeviceIdLSB() == deviceIdLSB)) {
                DeviceProfile newDeviceProfile;
                if (md.getSessionInfo().getDeviceProfileIdMSB() != deviceProfileIdMSB
                        || md.getSessionInfo().getDeviceProfileIdLSB() != deviceProfileIdLSB) {
                    //TODO: if transport types are different - we should close the session.
                    newDeviceProfile = deviceProfileCache.get(new DeviceProfileId(new UUID(deviceProfileIdMSB, deviceProfileIdLSB)));
                } else {
                    newDeviceProfile = null;
                }
                TransportProtos.SessionInfoProto newSessionInfo = TransportProtos.SessionInfoProto.newBuilder()
                        .mergeFrom(md.getSessionInfo())
                        .setDeviceProfileIdMSB(deviceProfileIdMSB)
                        .setDeviceProfileIdLSB(deviceProfileIdLSB)
                        .setDeviceName(device.getName())
                        .setDeviceType(device.getType()).build();
                if (device.getAdditionalInfo().has("gateway")
                        && device.getAdditionalInfo().get("gateway").asBoolean()
                        && device.getAdditionalInfo().has(OVERWRITE_ACTIVITY_TIME)
                        && device.getAdditionalInfo().get(OVERWRITE_ACTIVITY_TIME).isBoolean()) {
                    md.setOverwriteActivityTime(device.getAdditionalInfo().get(OVERWRITE_ACTIVITY_TIME).asBoolean());
                }
                md.setSessionInfo(newSessionInfo);
                transportCallbackExecutor.submit(() -> md.getListener().onDeviceUpdate(newSessionInfo, device, Optional.ofNullable(newDeviceProfile)));
            }
        });

        eventPublisher.publishEvent(new DeviceUpdatedEvent(device));
    }

    private void onDeviceDeleted(DeviceId deviceId) {
        sessions.forEach((id, md) -> {
            DeviceId sessionDeviceId = new DeviceId(new UUID(md.getSessionInfo().getDeviceIdMSB(), md.getSessionInfo().getDeviceIdLSB()));
            if (sessionDeviceId.equals(deviceId)) {
                transportCallbackExecutor.submit(() -> {
                    md.getListener().onDeviceDeleted(deviceId);
                });
            }
        });

        eventPublisher.publishEvent(new DeviceDeletedEvent(deviceId));
    }

    protected UUID toSessionId(TransportProtos.SessionInfoProto sessionInfo) {
        return new UUID(sessionInfo.getSessionIdMSB(), sessionInfo.getSessionIdLSB());
    }

    protected UUID getRoutingKey(TransportProtos.SessionInfoProto sessionInfo) {
        return new UUID(sessionInfo.getDeviceIdMSB(), sessionInfo.getDeviceIdLSB());
    }

    protected TenantId getTenantId(TransportProtos.SessionInfoProto sessionInfo) {
        return TenantId.fromUUID(new UUID(sessionInfo.getTenantIdMSB(), sessionInfo.getTenantIdLSB()));
    }

    protected CustomerId getCustomerId(TransportProtos.SessionInfoProto sessionInfo) {
        long msb = sessionInfo.getCustomerIdMSB();
        long lsb = sessionInfo.getCustomerIdLSB();
        if (msb != 0 && lsb != 0) {
            return new CustomerId(new UUID(msb, lsb));
        } else {
            return new CustomerId(EntityId.NULL_UUID);
        }
    }

    protected DeviceId getDeviceId(TransportProtos.SessionInfoProto sessionInfo) {
        return new DeviceId(new UUID(sessionInfo.getDeviceIdMSB(), sessionInfo.getDeviceIdLSB()));
    }

    private static TransportProtos.SessionEventMsg getSessionEventMsg(TransportProtos.SessionEvent event) {
        return TransportProtos.SessionEventMsg.newBuilder()
                .setSessionType(TransportProtos.SessionType.ASYNC)
                .setEvent(event).build();
    }

    protected void sendToDeviceActor(TransportProtos.SessionInfoProto sessionInfo, TransportToDeviceActorMsg toDeviceActorMsg, TransportServiceCallback<Void> callback) {
        TopicPartitionInfo tpi = partitionService.resolve(ServiceType.TB_CORE, getTenantId(sessionInfo), getDeviceId(sessionInfo));
        if (log.isTraceEnabled()) {
            log.trace("[{}][{}] Pushing to topic {} message {}", getTenantId(sessionInfo), getDeviceId(sessionInfo), tpi.getFullTopicName(), toDeviceActorMsg);
        }
        TransportTbQueueCallback transportTbQueueCallback = callback != null ?
                new TransportTbQueueCallback(callback) : null;
        tbCoreProducerStats.incrementTotal();
        StatsCallback wrappedCallback = new StatsCallback(transportTbQueueCallback, tbCoreProducerStats);
        tbCoreMsgProducer.send(tpi,
                new TbProtoQueueMsg<>(getRoutingKey(sessionInfo),
                        ToCoreMsg.newBuilder().setToDeviceActorMsg(toDeviceActorMsg).build()),
                wrappedCallback);
    }

    private void sendToRuleEngine(TenantId tenantId, TbMsg tbMsg, TbQueueCallback callback) {
        TopicPartitionInfo tpi = partitionService.resolve(ServiceType.TB_RULE_ENGINE, tbMsg.getQueueName(), tenantId, tbMsg.getOriginator());
        if (log.isTraceEnabled()) {
            log.trace("[{}][{}] Pushing to topic {} message {}", tenantId, tbMsg.getOriginator(), tpi.getFullTopicName(), tbMsg);
        }
        ToRuleEngineMsg msg = ToRuleEngineMsg.newBuilder().setTbMsg(TbMsg.toByteString(tbMsg))
                .setTenantIdMSB(tenantId.getId().getMostSignificantBits())
                .setTenantIdLSB(tenantId.getId().getLeastSignificantBits()).build();
        ruleEngineProducerStats.incrementTotal();
        StatsCallback wrappedCallback = new StatsCallback(callback, ruleEngineProducerStats);
        ruleEngineMsgProducer.send(tpi, new TbProtoQueueMsg<>(tbMsg.getId(), msg), wrappedCallback);
    }

    private void sendToRuleEngine(TenantId tenantId, DeviceId deviceId, CustomerId customerId, TransportProtos.SessionInfoProto sessionInfo, JsonObject json,
                                  TbMsgMetaData metaData, SessionMsgType sessionMsgType, TbQueueCallback callback) {
        DeviceProfileId deviceProfileId = new DeviceProfileId(new UUID(sessionInfo.getDeviceProfileIdMSB(), sessionInfo.getDeviceProfileIdLSB()));
        DeviceProfile deviceProfile = deviceProfileCache.get(deviceProfileId);
        RuleChainId ruleChainId;
        String queueName;

        if (deviceProfile == null) {
            log.warn("[{}] Device profile is null!", deviceProfileId);
            ruleChainId = null;
            queueName = null;
        } else {
            ruleChainId = deviceProfile.getDefaultRuleChainId();
            queueName = deviceProfile.getDefaultQueueName();
        }

        TbMsg tbMsg = TbMsg.newMsg(queueName, sessionMsgType.name(), deviceId, customerId, metaData, gson.toJson(json), ruleChainId, null);
        sendToRuleEngine(tenantId, tbMsg, callback);
    }

    private class TransportTbQueueCallback implements TbQueueCallback {
        private final TransportServiceCallback<Void> callback;

        private TransportTbQueueCallback(TransportServiceCallback<Void> callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(TbQueueMsgMetadata metadata) {
            DefaultTransportService.this.transportCallbackExecutor.submit(() -> callback.onSuccess(null));
        }

        @Override
        public void onFailure(Throwable t) {
            DefaultTransportService.this.transportCallbackExecutor.submit(() -> callback.onError(t));
        }
    }

    private static class StatsCallback implements TbQueueCallback {
        private final TbQueueCallback callback;
        private final MessagesStats stats;

        private StatsCallback(TbQueueCallback callback, MessagesStats stats) {
            this.callback = callback;
            this.stats = stats;
        }

        @Override
        public void onSuccess(TbQueueMsgMetadata metadata) {
            stats.incrementSuccessful();
            if (callback != null)
                callback.onSuccess(metadata);
        }

        @Override
        public void onFailure(Throwable t) {
            stats.incrementFailed();
            if (callback != null)
                callback.onFailure(t);
        }
    }

    private class MsgPackCallback implements TbQueueCallback {
        private final AtomicInteger msgCount;
        private final TransportServiceCallback<Void> callback;

        public MsgPackCallback(Integer msgCount, TransportServiceCallback<Void> callback) {
            this.msgCount = new AtomicInteger(msgCount);
            this.callback = callback;
        }

        @Override
        public void onSuccess(TbQueueMsgMetadata metadata) {
            if (msgCount.decrementAndGet() <= 0) {
                DefaultTransportService.this.transportCallbackExecutor.submit(() -> callback.onSuccess(null));
            }
        }

        @Override
        public void onFailure(Throwable t) {
            DefaultTransportService.this.transportCallbackExecutor.submit(() -> callback.onError(t));
        }
    }

    private class ApiStatsProxyCallback<T> implements TransportServiceCallback<T> {
        private final TenantId tenantId;
        private final CustomerId customerId;
        private final int dataPoints;
        private final TransportServiceCallback<T> callback;

        public ApiStatsProxyCallback(TenantId tenantId, CustomerId customerId, int dataPoints, TransportServiceCallback<T> callback) {
            this.tenantId = tenantId;
            this.customerId = customerId;
            this.dataPoints = dataPoints;
            this.callback = callback;
        }

        @Override
        public void onSuccess(T msg) {
            try {
                apiUsageClient.report(tenantId, customerId, ApiUsageRecordKey.TRANSPORT_MSG_COUNT, 1);
                apiUsageClient.report(tenantId, customerId, ApiUsageRecordKey.TRANSPORT_DP_COUNT, dataPoints);
            } finally {
                callback.onSuccess(msg);
            }
        }

        @Override
        public void onError(Throwable e) {
            callback.onError(e);
        }
    }

    @Override
    public ExecutorService getCallbackExecutor() {
        return transportCallbackExecutor;
    }

    @Override
    public boolean hasSession(TransportProtos.SessionInfoProto sessionInfo) {
        return sessions.containsKey(toSessionId(sessionInfo));
    }

    @Override
    public void createGaugeStats(String statsName, AtomicInteger number) {
        statsFactory.createGauge(StatsType.TRANSPORT + "." + statsName, number);
        statsMap.put(statsName, number);
    }

    @Scheduled(fixedDelayString = "${transport.stats.print-interval-ms:60000}")
    public void printStats() {
        if (statsEnabled && !statsMap.isEmpty()) {
            String values = statsMap.entrySet().stream()
                    .map(kv -> kv.getKey() + " [" + kv.getValue() + "]").collect(Collectors.joining(", "));
            log.info("Transport Stats: {}", values);
        }
    }
}
