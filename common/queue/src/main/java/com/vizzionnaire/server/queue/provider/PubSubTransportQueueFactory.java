package com.vizzionnaire.server.queue.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.gen.transport.TransportProtos.ToCoreMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToTransportMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiResponseMsg;
import com.vizzionnaire.server.queue.TbQueueAdmin;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.TbQueueProducer;
import com.vizzionnaire.server.queue.TbQueueRequestTemplate;
import com.vizzionnaire.server.queue.common.DefaultTbQueueRequestTemplate;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.discovery.TbServiceInfoProvider;
import com.vizzionnaire.server.queue.pubsub.TbPubSubAdmin;
import com.vizzionnaire.server.queue.pubsub.TbPubSubConsumerTemplate;
import com.vizzionnaire.server.queue.pubsub.TbPubSubProducerTemplate;
import com.vizzionnaire.server.queue.pubsub.TbPubSubSettings;
import com.vizzionnaire.server.queue.pubsub.TbPubSubSubscriptionSettings;
import com.vizzionnaire.server.queue.settings.TbQueueCoreSettings;
import com.vizzionnaire.server.queue.settings.TbQueueRuleEngineSettings;
import com.vizzionnaire.server.queue.settings.TbQueueTransportApiSettings;
import com.vizzionnaire.server.queue.settings.TbQueueTransportNotificationSettings;

import javax.annotation.PreDestroy;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='pubsub' && (('${service.type:null}'=='monolith' && '${transport.api_enabled:true}'=='true') || '${service.type:null}'=='tb-transport')")
@Slf4j
public class PubSubTransportQueueFactory implements TbTransportQueueFactory {

    private final TbPubSubSettings pubSubSettings;
    private final TbServiceInfoProvider serviceInfoProvider;
    private final TbQueueCoreSettings coreSettings;
    private final TbQueueRuleEngineSettings ruleEngineSettings;
    private final TbQueueTransportApiSettings transportApiSettings;
    private final TbQueueTransportNotificationSettings transportNotificationSettings;

    private final TbQueueAdmin coreAdmin;
    private final TbQueueAdmin ruleEngineAdmin;
    private final TbQueueAdmin transportApiAdmin;
    private final TbQueueAdmin notificationAdmin;

    public PubSubTransportQueueFactory(TbPubSubSettings pubSubSettings,
                                       TbServiceInfoProvider serviceInfoProvider,
                                       TbQueueCoreSettings coreSettings,
                                       TbQueueRuleEngineSettings ruleEngineSettings,
                                       TbQueueTransportApiSettings transportApiSettings,
                                       TbQueueTransportNotificationSettings transportNotificationSettings,
                                       TbPubSubSubscriptionSettings pubSubSubscriptionSettings) {
        this.pubSubSettings = pubSubSettings;
        this.serviceInfoProvider = serviceInfoProvider;
        this.coreSettings = coreSettings;
        this.ruleEngineSettings = ruleEngineSettings;
        this.transportApiSettings = transportApiSettings;
        this.transportNotificationSettings = transportNotificationSettings;

        this.coreAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getCoreSettings());
        this.ruleEngineAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getRuleEngineSettings());
        this.transportApiAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getTransportApiSettings());
        this.notificationAdmin = new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getNotificationsSettings());
    }

    @Override
    public TbQueueRequestTemplate<TbProtoQueueMsg<TransportApiRequestMsg>, TbProtoQueueMsg<TransportApiResponseMsg>> createTransportApiRequestTemplate() {
        TbQueueProducer<TbProtoQueueMsg<TransportApiRequestMsg>> producer = new TbPubSubProducerTemplate<>(transportApiAdmin, pubSubSettings, transportApiSettings.getRequestsTopic());
        TbQueueConsumer<TbProtoQueueMsg<TransportApiResponseMsg>> consumer = new TbPubSubConsumerTemplate<>(transportApiAdmin, pubSubSettings,
                transportApiSettings.getResponsesTopic() + "." + serviceInfoProvider.getServiceId(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), TransportApiResponseMsg.parseFrom(msg.getData()), msg.getHeaders()));

        DefaultTbQueueRequestTemplate.DefaultTbQueueRequestTemplateBuilder
                <TbProtoQueueMsg<TransportApiRequestMsg>, TbProtoQueueMsg<TransportApiResponseMsg>> templateBuilder = DefaultTbQueueRequestTemplate.builder();
        templateBuilder.queueAdmin(transportApiAdmin);
        templateBuilder.requestTemplate(producer);
        templateBuilder.responseTemplate(consumer);
        templateBuilder.maxPendingRequests(transportApiSettings.getMaxPendingRequests());
        templateBuilder.maxRequestTimeout(transportApiSettings.getMaxRequestsTimeout());
        templateBuilder.pollInterval(transportApiSettings.getResponsePollInterval());
        return templateBuilder.build();
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> createRuleEngineMsgProducer() {
        return new TbPubSubProducerTemplate<>(ruleEngineAdmin, pubSubSettings, ruleEngineSettings.getTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> createTbCoreMsgProducer() {
        return new TbPubSubProducerTemplate<>(coreAdmin, pubSubSettings, coreSettings.getTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToTransportMsg>> createTransportNotificationsConsumer() {
        return new TbPubSubConsumerTemplate<>(notificationAdmin, pubSubSettings,
                transportNotificationSettings.getNotificationsTopic() + "." + serviceInfoProvider.getServiceId(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), ToTransportMsg.parseFrom(msg.getData()), msg.getHeaders()));
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        return new TbPubSubProducerTemplate<>(coreAdmin, pubSubSettings, coreSettings.getUsageStatsTopic());
    }

    @PreDestroy
    private void destroy() {
        if (coreAdmin != null) {
            coreAdmin.destroy();
        }
        if (ruleEngineAdmin != null) {
            ruleEngineAdmin.destroy();
        }
        if (transportApiAdmin != null) {
            transportApiAdmin.destroy();
        }
        if (notificationAdmin != null) {
            notificationAdmin.destroy();
        }
    }
}
