package com.vizzionnaire.server.queue.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.queue.Queue;
import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.gen.js.JsInvokeProtos;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.TbQueueProducer;
import com.vizzionnaire.server.queue.TbQueueRequestTemplate;
import com.vizzionnaire.server.queue.common.TbProtoJsQueueMsg;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.discovery.NotificationsTopicService;
import com.vizzionnaire.server.queue.discovery.TbServiceInfoProvider;
import com.vizzionnaire.server.queue.memory.InMemoryStorage;
import com.vizzionnaire.server.queue.memory.InMemoryTbQueueConsumer;
import com.vizzionnaire.server.queue.memory.InMemoryTbQueueProducer;
import com.vizzionnaire.server.queue.settings.TbQueueCoreSettings;
import com.vizzionnaire.server.queue.settings.TbQueueRuleEngineSettings;
import com.vizzionnaire.server.queue.settings.TbQueueTransportApiSettings;
import com.vizzionnaire.server.queue.settings.TbQueueTransportNotificationSettings;
import com.vizzionnaire.server.queue.settings.TbQueueVersionControlSettings;

@Slf4j
@Component
@ConditionalOnExpression("'${queue.type:null}'=='in-memory' && '${service.type:null}'=='monolith'")
public class InMemoryMonolithQueueFactory implements TbCoreQueueFactory, TbRuleEngineQueueFactory, TbVersionControlQueueFactory {

    private final NotificationsTopicService notificationsTopicService;
    private final TbQueueCoreSettings coreSettings;
    private final TbServiceInfoProvider serviceInfoProvider;
    private final TbQueueRuleEngineSettings ruleEngineSettings;
    private final TbQueueVersionControlSettings vcSettings;
    private final TbQueueTransportApiSettings transportApiSettings;
    private final TbQueueTransportNotificationSettings transportNotificationSettings;
    private final InMemoryStorage storage;

    public InMemoryMonolithQueueFactory(NotificationsTopicService notificationsTopicService, TbQueueCoreSettings coreSettings,
                                        TbQueueRuleEngineSettings ruleEngineSettings,
                                        TbQueueVersionControlSettings vcSettings,
                                        TbServiceInfoProvider serviceInfoProvider,
                                        TbQueueTransportApiSettings transportApiSettings,
                                        TbQueueTransportNotificationSettings transportNotificationSettings,
                                        InMemoryStorage storage) {
        this.notificationsTopicService = notificationsTopicService;
        this.coreSettings = coreSettings;
        this.vcSettings = vcSettings;
        this.serviceInfoProvider = serviceInfoProvider;
        this.ruleEngineSettings = ruleEngineSettings;
        this.transportApiSettings = transportApiSettings;
        this.transportNotificationSettings = transportNotificationSettings;
        this.storage = storage;
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToTransportMsg>> createTransportNotificationsMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, transportNotificationSettings.getNotificationsTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> createRuleEngineMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, ruleEngineSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToRuleEngineNotificationMsg>> createRuleEngineNotificationsMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, ruleEngineSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToCoreMsg>> createTbCoreMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, coreSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, coreSettings.getTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToVersionControlServiceMsg>> createToVersionControlMsgConsumer() {
        return new InMemoryTbQueueConsumer<>(storage, vcSettings.getTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>> createToRuleEngineMsgConsumer(Queue configuration) {
        return new InMemoryTbQueueConsumer<>(storage, configuration.getTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToRuleEngineNotificationMsg>> createToRuleEngineNotificationsMsgConsumer() {
        return new InMemoryTbQueueConsumer<>(storage, notificationsTopicService.getNotificationsTopic(ServiceType.TB_RULE_ENGINE, serviceInfoProvider.getServiceId()).getFullTopicName());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToCoreMsg>> createToCoreMsgConsumer() {
        return new InMemoryTbQueueConsumer<>(storage, coreSettings.getTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToCoreNotificationMsg>> createToCoreNotificationsMsgConsumer() {
        return new InMemoryTbQueueConsumer<>(storage, notificationsTopicService.getNotificationsTopic(ServiceType.TB_CORE, serviceInfoProvider.getServiceId()).getFullTopicName());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.TransportApiRequestMsg>> createTransportApiRequestConsumer() {
        return new InMemoryTbQueueConsumer<>(storage, transportApiSettings.getRequestsTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.TransportApiResponseMsg>> createTransportApiResponseProducer() {
        return new InMemoryTbQueueProducer<>(storage, transportApiSettings.getResponsesTopic());
    }

    @Override
    public TbQueueRequestTemplate<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>, TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> createRemoteJsRequestTemplate() {
        return null;
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgConsumer() {
        return new InMemoryTbQueueConsumer<>(storage, coreSettings.getUsageStatsTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgConsumer() {
        return new InMemoryTbQueueConsumer<>(storage, coreSettings.getOtaPackageTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, coreSettings.getOtaPackageTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, coreSettings.getUsageStatsTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToVersionControlServiceMsg>> createVersionControlMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, vcSettings.getTopic());
    }

    @Scheduled(fixedRateString = "${queue.in_memory.stats.print-interval-ms:60000}")
    private void printInMemoryStats() {
        storage.printStats();
    }


}
