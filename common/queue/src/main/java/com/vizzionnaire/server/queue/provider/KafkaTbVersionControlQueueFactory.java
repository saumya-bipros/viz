package com.vizzionnaire.server.queue.provider;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToVersionControlServiceMsg;
import com.vizzionnaire.server.queue.TbQueueAdmin;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.TbQueueProducer;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.discovery.TbServiceInfoProvider;
import com.vizzionnaire.server.queue.kafka.TbKafkaAdmin;
import com.vizzionnaire.server.queue.kafka.TbKafkaConsumerStatsService;
import com.vizzionnaire.server.queue.kafka.TbKafkaConsumerTemplate;
import com.vizzionnaire.server.queue.kafka.TbKafkaProducerTemplate;
import com.vizzionnaire.server.queue.kafka.TbKafkaSettings;
import com.vizzionnaire.server.queue.kafka.TbKafkaTopicConfigs;
import com.vizzionnaire.server.queue.settings.TbQueueCoreSettings;
import com.vizzionnaire.server.queue.settings.TbQueueVersionControlSettings;

import javax.annotation.PreDestroy;

@Component
@ConditionalOnExpression("'${queue.type:null}'=='kafka' && '${service.type:null}'=='tb-vc-executor'")
public class KafkaTbVersionControlQueueFactory implements TbVersionControlQueueFactory {

    private final TbKafkaSettings kafkaSettings;
    private final TbServiceInfoProvider serviceInfoProvider;
    private final TbQueueCoreSettings coreSettings;
    private final TbQueueVersionControlSettings vcSettings;
    private final TbKafkaConsumerStatsService consumerStatsService;

    private final TbQueueAdmin coreAdmin;
    private final TbQueueAdmin vcAdmin;
    private final TbQueueAdmin notificationAdmin;

    public KafkaTbVersionControlQueueFactory(TbKafkaSettings kafkaSettings,
                                             TbServiceInfoProvider serviceInfoProvider,
                                             TbQueueCoreSettings coreSettings,
                                             TbQueueVersionControlSettings vcSettings,
                                             TbKafkaConsumerStatsService consumerStatsService,
                                             TbKafkaTopicConfigs kafkaTopicConfigs) {
        this.kafkaSettings = kafkaSettings;
        this.serviceInfoProvider = serviceInfoProvider;
        this.coreSettings = coreSettings;
        this.vcSettings = vcSettings;
        this.consumerStatsService = consumerStatsService;

        this.coreAdmin = new TbKafkaAdmin(kafkaSettings, kafkaTopicConfigs.getCoreConfigs());
        this.vcAdmin = new TbKafkaAdmin(kafkaSettings, kafkaTopicConfigs.getVcConfigs());
        this.notificationAdmin = new TbKafkaAdmin(kafkaSettings, kafkaTopicConfigs.getNotificationsConfigs());
    }


    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer() {
        TbKafkaProducerTemplate.TbKafkaProducerTemplateBuilder<TbProtoQueueMsg<ToCoreNotificationMsg>> requestBuilder = TbKafkaProducerTemplate.builder();
        requestBuilder.settings(kafkaSettings);
        requestBuilder.clientId("tb-vc-to-core-notifications-" + serviceInfoProvider.getServiceId());
        requestBuilder.defaultTopic(coreSettings.getTopic());
        requestBuilder.admin(notificationAdmin);
        return requestBuilder.build();
    }

    @Override
    public TbQueueConsumer<TbProtoQueueMsg<ToVersionControlServiceMsg>> createToVersionControlMsgConsumer() {
        TbKafkaConsumerTemplate.TbKafkaConsumerTemplateBuilder<TbProtoQueueMsg<ToVersionControlServiceMsg>> consumerBuilder = TbKafkaConsumerTemplate.builder();
        consumerBuilder.settings(kafkaSettings);
        consumerBuilder.topic(vcSettings.getTopic());
        consumerBuilder.clientId("tb-vc-consumer-" + serviceInfoProvider.getServiceId());
        consumerBuilder.groupId("tb-vc-node");
        consumerBuilder.decoder(msg -> new TbProtoQueueMsg<>(msg.getKey(), ToVersionControlServiceMsg.parseFrom(msg.getData()), msg.getHeaders()));
        consumerBuilder.admin(vcAdmin);
        consumerBuilder.statsService(consumerStatsService);
        return consumerBuilder.build();
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer() {
        TbKafkaProducerTemplate.TbKafkaProducerTemplateBuilder<TbProtoQueueMsg<ToUsageStatsServiceMsg>> requestBuilder = TbKafkaProducerTemplate.builder();
        requestBuilder.settings(kafkaSettings);
        requestBuilder.clientId("tb-vc-us-producer-" + serviceInfoProvider.getServiceId());
        requestBuilder.defaultTopic(coreSettings.getUsageStatsTopic());
        requestBuilder.admin(coreAdmin);
        return requestBuilder.build();
    }

    @PreDestroy
    private void destroy() {
        if (coreAdmin != null) {
            coreAdmin.destroy();
        }
        if (vcAdmin != null) {
            vcAdmin.destroy();
        }
        if (notificationAdmin != null) {
            notificationAdmin.destroy();
        }
    }
}
