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
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusAdmin;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusConsumerTemplate;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusProducerTemplate;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusQueueConfigs;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusSettings;
import com.vizzionnaire.server.queue.common.DefaultTbQueueRequestTemplate;
import com.vizzionnaire.server.queue.common.TbProtoJsQueueMsg;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.discovery.NotificationsTopicService;
import com.vizzionnaire.server.queue.discovery.TbServiceInfoProvider;
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
@ConditionalOnExpression("'${queue.type:null}'=='service-bus' && '${service.type:null}'=='tb-core'")
public class ServiceBusTbCoreQueueFactory implements TbCoreQueueFactory {

    private final TbServiceBusSettings serviceBusSettings;
    private final TbQueueRuleEngineSettings ruleEngineSettings;
    private final TbQueueCoreSettings coreSettings;
    private final TbQueueTransportApiSettings transportApiSettings;
    private final NotificationsTopicService notificationsTopicService;
    private final TbServiceInfoProvider serviceInfoProvider;
    private final TbQueueRemoteJsInvokeSettings jsInvokeSettings;
    private final TbQueueTransportNotificationSettings transportNotificationSettings;

    private final TbQueueAdmin coreAdmin;
    private final TbQueueAdmin ruleEngineAdmin;
    private final TbQueueAdmin jsExecutorAdmin;
    private final TbQueueAdmin transportApiAdmin;
    private final TbQueueAdmin notificationAdmin;

    public ServiceBusTbCoreQueueFactory(TbServiceBusSettings serviceBusSettings,
                                        TbQueueCoreSettings coreSettings,
                                        TbQueueTransportApiSettings transportApiSettings,
                                        TbQueueRuleEngineSettings ruleEngineSettings,
                                        NotificationsTopicService notificationsTopicService,
                                        TbServiceInfoProvider serviceInfoProvider,
                                        TbQueueRemoteJsInvokeSettings jsInvokeSettings,
                                        TbQueueTransportNotificationSettings transportNotificationSettings,
                                        TbServiceBusQueueConfigs serviceBusQueueConfigs) {
        this.serviceBusSettings = serviceBusSettings;
        this.coreSettings = coreSettings;
        this.transportApiSettings = transportApiSettings;
        this.ruleEngineSettings = ruleEngineSettings;
        this.notificationsTopicService = notificationsTopicService;
        this.serviceInfoProvider = serviceInfoProvider;
        this.jsInvokeSettings = jsInvokeSettings;
        this.transportNotificationSettings = transportNotificationSettings;

        this.coreAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getCoreConfigs());
        this.ruleEngineAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getRuleEngineConfigs());
        this.jsExecutorAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getJsExecutorConfigs());
        this.transportApiAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getTransportApiConfigs());
        this.notificationAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getNotificationsConfigs());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToTransportMsg>> createTransportNotificationsMsgProducer() {
        return new TbServiceBusProducerTemplate<>(notificationAdmin, serviceBusSettings, transportNotificationSettings.getNotificationsTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> createRuleEngineMsgProducer() {
        return new TbServiceBusProducerTemplate<>(coreAdmin, serviceBusSettings, coreSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> createRuleEngineNotificationsMsgProducer() {
        return new TbServiceBusProducerTemplate<>(notificationAdmin, serviceBusSettings, ruleEngineSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> createTbCoreMsgProducer() {
        return new TbServiceBusProducerTemplate<>(coreAdmin, serviceBusSettings, coreSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer() {
        return new TbServiceBusProducerTemplate<>(notificationAdmin, serviceBusSettings, coreSettings.getTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToCoreMsg>> createToCoreMsgConsumer() {
        return new TbServiceBusConsumerTemplate<>(coreAdmin, serviceBusSettings, coreSettings.getTopic(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToCoreMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToCoreNotificationMsg>> createToCoreNotificationsMsgConsumer() {
        return new TbServiceBusConsumerTemplate<>(notificationAdmin, serviceBusSettings,
                notificationsTopicService.getNotificationsTopic(ServiceType.TB_CORE, serviceInfoProvider.getServiceId()).getFullTopicName(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToCoreNotificationMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportApiRequestMsg>> createTransportApiRequestConsumer() {
        return new TbServiceBusConsumerTemplate<>(transportApiAdmin, serviceBusSettings, transportApiSettings.getRequestsTopic(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), TransportApiRequestMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportApiResponseMsg>> createTransportApiResponseProducer() {
        return new TbServiceBusProducerTemplate<>(transportApiAdmin, serviceBusSettings, transportApiSettings.getResponsesTopic());
    }

    @Override
    @Bean
    public TbQueueRequestTemplate<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>, TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> createRemoteJsRequestTemplate() {
        TbQueueProducer<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>> producer = new TbServiceBusProducerTemplate<>(jsExecutorAdmin, serviceBusSettings, jsInvokeSettings.getRequestTopic());
        TbQueueConsumer<TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> consumer = new TbServiceBusConsumerTemplate<>(jsExecutorAdmin, serviceBusSettings,
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
        return new TbServiceBusConsumerTemplate<>(coreAdmin, serviceBusSettings, coreSettings.getUsageStatsTopic(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToUsageStatsServiceMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgConsumer() {
        return new TbServiceBusConsumerTemplate<>(coreAdmin, serviceBusSettings, coreSettings.getOtaPackageTopic(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToOtaPackageStateServiceMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgProducer() {
        return new TbServiceBusProducerTemplate<>(coreAdmin, serviceBusSettings, coreSettings.getOtaPackageTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        return new TbServiceBusProducerTemplate<>(coreAdmin, serviceBusSettings, coreSettings.getUsageStatsTopic());
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
        if (ruleEngineAdmin != null) {
            ruleEngineAdmin.destroy();
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
    }
}
