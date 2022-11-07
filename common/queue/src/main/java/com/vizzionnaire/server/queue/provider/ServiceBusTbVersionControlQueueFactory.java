package com.vizzionnaire.server.queue.provider;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.TbQueueAdmin;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.TbQueueProducer;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusAdmin;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusConsumerTemplate;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusProducerTemplate;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusQueueConfigs;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusSettings;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.settings.TbQueueCoreSettings;
import com.vizzionnaire.server.queue.settings.TbQueueVersionControlSettings;

import javax.annotation.PreDestroy;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='service-bus' && '${service.type:null}'=='tb-vc-executor'")
public class ServiceBusTbVersionControlQueueFactory implements TbVersionControlQueueFactory {

    private final TbServiceBusSettings serviceBusSettings;
    private final TbQueueCoreSettings coreSettings;
    private final TbQueueVersionControlSettings vcSettings;

    private final TbQueueAdmin coreAdmin;
    private final TbQueueAdmin notificationAdmin;
    private final TbQueueAdmin vcAdmin;

    public ServiceBusTbVersionControlQueueFactory(TbServiceBusSettings serviceBusSettings,
                                                  TbQueueCoreSettings coreSettings,
                                                  TbQueueVersionControlSettings vcSettings,
                                                  TbServiceBusQueueConfigs serviceBusQueueConfigs
    ) {
        this.serviceBusSettings = serviceBusSettings;
        this.coreSettings = coreSettings;
        this.vcSettings = vcSettings;

        this.coreAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getCoreConfigs());
        this.notificationAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getNotificationsConfigs());
        this.vcAdmin = new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getVcConfigs());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        return new TbServiceBusProducerTemplate<>(coreAdmin, serviceBusSettings, coreSettings.getUsageStatsTopic());
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer() {
        return new TbServiceBusProducerTemplate<>(notificationAdmin, serviceBusSettings, coreSettings.getTopic());
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToVersionControlServiceMsg>> createToVersionControlMsgConsumer() {
        return new TbServiceBusConsumerTemplate<>(vcAdmin, serviceBusSettings, vcSettings.getTopic(),
                msg -> new TbProtoQueueMsg<>(msg.getKey(), TransportProtos.ToVersionControlServiceMsg.parseFrom(msg.getData()), msg.getHeaders())
        );
    }

    @PreDestroy
    private void destroy() {
        if (coreAdmin != null) {
            coreAdmin.destroy();
        }
        if (notificationAdmin != null) {
            notificationAdmin.destroy();
        }
        if (vcAdmin != null) {
            vcAdmin.destroy();
        }
    }
}
