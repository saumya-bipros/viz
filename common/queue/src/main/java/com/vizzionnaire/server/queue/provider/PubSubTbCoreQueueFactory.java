package com.vizzionnaire.server.queue.provider;

import com.google.protobuf.util.JsonFormat;
import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.gen.js.JsInvokeProtos;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToCoreMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToOtaPackageStateServiceMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToRuleEngineNotificationMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToTransportMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiResponseMsg;
import com.vizzionnaire.server.queue.TbQueueAdmin;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.TbQueueProducer;
import com.vizzionnaire.server.queue.TbQueueRequestTemplate;
import com.vizzionnaire.server.queue.common.DefaultTbQueueRequestTemplate;
import com.vizzionnaire.server.queue.common.TbProtoJsQueueMsg;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.discovery.NotificationsTopicService;
import com.vizzionnaire.server.queue.discovery.TbServiceInfoProvider;
import com.vizzionnaire.server.queue.pubsub.TbPubSubAdmin;
import com.vizzionnaire.server.queue.pubsub.TbPubSubConsumerTemplate;
import com.vizzionnaire.server.queue.pubsub.TbPubSubProducerTemplate;
import com.vizzionnaire.server.queue.pubsub.TbPubSubSettings;
import com.vizzionnaire.server.queue.pubsub.TbPubSubSubscriptionSettings;
import com.vizzionnaire.server.queue.settings.TbQueueCoreSettings;
import com.vizzionnaire.server.queue.settings.TbQueueRemoteJsInvokeSettings;
import com.vizzionnaire.server.queue.settings.TbQueueRuleEngineSettings;
import com.vizzionnaire.server.queue.settings.TbQueueTransportApiSettings;
import com.vizzionnaire.server.queue.settings.TbQueueTransportNotificationSettings;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='pubsub' && '${service.type:null}'=='tb-core'")
public class PubSubTbCoreQueueFactory implements TbCoreQueueFactory {

    private final TbPubSubSettings pubSubSettings;
    private final TbQueueCoreSettings coreSettings;
    private final TbQueueTransportApiSettings transportApiSettings;
    private final NotificationsTopicService notificationsTopicService;
    private final TbServiceInfoProvider serviceInfoProvider;
    private final TbQueueRemoteJsInvokeSettings jsInvokeSettings;
    private final TbQueueTransportNotificationSettings transportNotificationSettings;
    private final TbQueueRuleEngineSettings ruleEngineSettings;

    private final TbQueueAdmin coreAdmin;
    private final TbQueueAdmin jsExecutorAdmin;
    private final TbQueueAdmin transportApiAdmin;
    private final TbQueueAdmin notificationAdmin;
    private final TbQueueAdmin ruleEngineAdmin;

    public PubSubTbCoreQueueFactory(TbPubSubSettings pubSubSettings,
                                    TbQueueCoreSettings coreSettings,
                                    TbQueueTransportApiSettings transportApiSettings,
                                    NotificationsTopicService notificationsTopicService,
                                    TbServiceInfoProvider serviceInfoProvider,
                                    TbQueueRemoteJsInvokeSettings jsInvokeSettings,
                                    TbQueueTransportNotificationSettings transportNotificationSettings,
                                    TbQueueRuleEngineSettings ruleEngineSettings,
                                    TbPubSubSubscriptionSettings pubSubSubscriptionSettings) {
        this.pubSubSettings = pubSubSettings;
        this.coreSettings = coreSettings;
        this.transportApiSettings = transportApiSettings;
        this.notificationsTopicService = notificationsTopicService;
        this.serviceInfoProvider = serviceInfoProvider;
        this.jsInvokeSettings = jsInvokeSettings;
        this.transportNotificationSettings = transportNotificationSettings;
        this.ruleEngineSettings = ruleEngineSettings;

        this.coreAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getCoreSettings());
        this.jsExecutorAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getJsExecutorSettings());
        this.transportApiAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getTransportApiSettings());
        this.notificationAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getNotificationsSettings());
        this.ruleEngineAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getRuleEngineSettings());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToTransportMsg>> createTransportNotificationsMsgProducer() {
        return new TbPubSubProducerTemplate<>(notificationAdmin, pubSubSettings, transportNotificationSettings.getNotificationsTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> createRuleEngineMsgProducer() {
        return new TbPubSubProducerTemplate<>(coreAdmin, pubSubSettings, coreSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> createRuleEngineNotificationsMsgProducer() {
        return new TbPubSubProducerTemplate<>(notificationAdmin, pubSubSettings, ruleEngineSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> createTbCoreMsgProducer() {
        return new TbPubSubProducerTemplate<>(coreAdmin, pubSubSettings, coreSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer() {
        return new TbPubSubProducerTemplate<>(notificationAdmin, pubSubSettings, coreSettings.getTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToCoreMsg>> createToCoreMsgConsumer() {
        return new TbPubSubConsumerTemplate<>(coreAdmin, pubSubSettings, coreSettings.getTopic(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToCoreMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToCoreNotificationMsg>> createToCoreNotificationsMsgConsumer() {
        return new TbPubSubConsumerTemplate<>(notificationAdmin, pubSubSettings,
                notificationsTopicService.getNotificationsTopic(ServiceType.TB_CORE, serviceInfoProvider.getServiceId()).getFullTopicName(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToCoreNotificationMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportApiRequestMsg>> createTransportApiRequestConsumer() {
        return new TbPubSubConsumerTemplate<>(transportApiAdmin, pubSubSettings, transportApiSettings.getRequestsTopic(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), TransportApiRequestMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportApiResponseMsg>> createTransportApiResponseProducer() {
        return new TbPubSubProducerTemplate<>(transportApiAdmin, pubSubSettings, transportApiSettings.getResponsesTopic());
    }

    @Override
    @Bean
    public TbQueueRequestTemplate<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>, TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> createRemoteJsRequestTemplate() {
        TbQueueProducer<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>> producer = new TbPubSubProducerTemplate<>(jsExecutorAdmin, pubSubSettings, jsInvokeSettings.getRequestTopic());
        TbQueueConsumer<TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> consumer = new TbPubSubConsumerTemplate<>(jsExecutorAdmin, pubSubSettings,
                jsInvokeSettings.getResponseTopic() + "." + serviceInfoProvider.getServiceId(),
                msg -> {
                    JsInvokeProtos.RemoteJsResponse.Builder builder = JsInvokeProtos.RemoteJsResponse.newBuilder();
                    JsonFormat.parser().ignoringUnknownFields().merge(new String(msg.getData(), StandardCharsets.UTF_8), builder);
                    return new TbProtoQueueMsg<>(msg.getKey(), builder.build(), msg.getHeaders());
                });

        DefaultTbQueueRequestTemplate.DefaultTbQueueRequestTemplateBuilder
                <TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>, TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> builder = DefaultTbQueueRequestTemplate.builder();
        builder.queueAdmin(jsExecutorAdmin);
        builder.requestTemplate(producer);
        builder.responseTemplate(consumer);
        builder.maxPendingRequests(jsInvokeSettings.getMaxPendingRequests());
        builder.maxRequestTimeout(jsInvokeSettings.getMaxRequestsTimeout());
        builder.pollInterval(jsInvokeSettings.getResponsePollInterval());
        return builder.build();
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgConsumer() {
        return new TbPubSubConsumerTemplate<>(coreAdmin, pubSubSettings, coreSettings.getUsageStatsTopic(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToUsageStatsServiceMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgConsumer() {
        return new TbPubSubConsumerTemplate<>(coreAdmin, pubSubSettings, coreSettings.getOtaPackageTopic(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToOtaPackageStateServiceMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgProducer() {
        return new TbPubSubProducerTemplate<>(coreAdmin, pubSubSettings, coreSettings.getOtaPackageTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        return new TbPubSubProducerTemplate<>(coreAdmin, pubSubSettings, coreSettings.getUsageStatsTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToVersionControlServiceMsg>> createVersionControlMsgProducer() {
        //TODO: version-control
        return null;
    }

    @PreDestroy
    private void destroy() {
        if (coreAdmin != null) {
            coreAdmin.destroy();
        }
        if (jsExecutorAdmin != null) {
            jsExecutorAdmin.destroy();
        }
        if (transportApiAdmin != null) {
            transportApiAdmin.destroy();
        }
        if (notificationAdmin != null) {
            notificationAdmin.destroy();
        }
        if (ruleEngineAdmin != null) {
            ruleEngineAdmin.destroy();
        }
    }
}
