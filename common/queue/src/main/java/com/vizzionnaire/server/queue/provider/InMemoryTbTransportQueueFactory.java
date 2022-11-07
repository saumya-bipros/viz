package com.vizzionnaire.server.queue.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToCoreMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToTransportMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiResponseMsg;
import com.vizzionnaire.server.queue.TbQueueAdmin;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.TbQueueProducer;
import com.vizzionnaire.server.queue.TbQueueRequestTemplate;
import com.vizzionnaire.server.queue.common.DefaultTbQueueRequestTemplate;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.discovery.TbServiceInfoProvider;
import com.vizzionnaire.server.queue.memory.InMemoryStorage;
import com.vizzionnaire.server.queue.memory.InMemoryTbQueueConsumer;
import com.vizzionnaire.server.queue.memory.InMemoryTbQueueProducer;
import com.vizzionnaire.server.queue.settings.TbQueueCoreSettings;
import com.vizzionnaire.server.queue.settings.TbQueueTransportApiSettings;
import com.vizzionnaire.server.queue.settings.TbQueueTransportNotificationSettings;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='in-memory' && (('${service.type:null}'=='monolith' && '${transport.api_enabled:true}'=='true') || '${service.type:null}'=='tb-transport')")
@Slf4j
public class InMemoryTbTransportQueueFactory implements TbTransportQueueFactory {
    private final TbQueueTransportApiSettings transportApiSettings;
    private final TbQueueTransportNotificationSettings transportNotificationSettings;
    private final TbServiceInfoProvider serviceInfoProvider;
    private final TbQueueCoreSettings coreSettings;
    private final InMemoryStorage storage;

    public InMemoryTbTransportQueueFactory(TbQueueTransportApiSettings transportApiSettings,
                                           TbQueueTransportNotificationSettings transportNotificationSettings,
                                           TbServiceInfoProvider serviceInfoProvider,
                                           TbQueueCoreSettings coreSettings,
                                           InMemoryStorage storage) {
        this.transportApiSettings = transportApiSettings;
        this.transportNotificationSettings = transportNotificationSettings;
        this.serviceInfoProvider = serviceInfoProvider;
        this.coreSettings = coreSettings;
        this.storage = storage;
    }

    @Override
    public TbQueueRequestTemplate<TbProtoQueueMsg<TransportApiRequestMsg>, TbProtoQueueMsg<TransportApiResponseMsg>> createTransportApiRequestTemplate() {
        InMemoryTbQueueProducer<TbProtoQueueMsg<TransportApiRequestMsg>> producerTemplate =
                new InMemoryTbQueueProducer<>(storage, transportApiSettings.getRequestsTopic());

        InMemoryTbQueueConsumer<TbProtoQueueMsg<TransportApiResponseMsg>> consumerTemplate =
                new InMemoryTbQueueConsumer<>(storage, transportApiSettings.getResponsesTopic() + "." + serviceInfoProvider.getServiceId());

        DefaultTbQueueRequestTemplate.DefaultTbQueueRequestTemplateBuilder
                <TbProtoQueueMsg<TransportApiRequestMsg>, TbProtoQueueMsg<TransportApiResponseMsg>> templateBuilder = DefaultTbQueueRequestTemplate.builder();

        templateBuilder.queueAdmin(new TbQueueAdmin() {
            @Override
            public void createTopicIfNotExists(String topic) {}

            @Override
            public void destroy() {}

            @Override
            public void deleteTopic(String topic) {}
        });

        templateBuilder.requestTemplate(producerTemplate);
        templateBuilder.responseTemplate(consumerTemplate);
        templateBuilder.maxPendingRequests(transportApiSettings.getMaxPendingRequests());
        templateBuilder.maxRequestTimeout(transportApiSettings.getMaxRequestsTimeout());
        templateBuilder.pollInterval(transportApiSettings.getResponsePollInterval());
        return templateBuilder.build();
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> createRuleEngineMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, transportApiSettings.getRequestsTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> createTbCoreMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, coreSettings.getTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToTransportMsg>> createTransportNotificationsConsumer() {
        return new InMemoryTbQueueConsumer<>(storage, transportNotificationSettings.getNotificationsTopic() + "." + serviceInfoProvider.getServiceId());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        return new InMemoryTbQueueProducer<>(storage, coreSettings.getUsageStatsTopic());
    }

}
