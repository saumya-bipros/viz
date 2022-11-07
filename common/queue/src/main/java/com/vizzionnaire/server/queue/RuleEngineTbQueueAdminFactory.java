package com.vizzionnaire.server.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vizzionnaire.server.queue.TbQueueAdmin;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusAdmin;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusQueueConfigs;
import com.vizzionnaire.server.queue.azure.servicebus.TbServiceBusSettings;
import com.vizzionnaire.server.queue.kafka.TbKafkaAdmin;
import com.vizzionnaire.server.queue.kafka.TbKafkaSettings;
import com.vizzionnaire.server.queue.kafka.TbKafkaTopicConfigs;
import com.vizzionnaire.server.queue.pubsub.TbPubSubAdmin;
import com.vizzionnaire.server.queue.pubsub.TbPubSubSettings;
import com.vizzionnaire.server.queue.pubsub.TbPubSubSubscriptionSettings;
import com.vizzionnaire.server.queue.rabbitmq.TbRabbitMqAdmin;
import com.vizzionnaire.server.queue.rabbitmq.TbRabbitMqQueueArguments;
import com.vizzionnaire.server.queue.rabbitmq.TbRabbitMqSettings;
import com.vizzionnaire.server.queue.sqs.TbAwsSqsAdmin;
import com.vizzionnaire.server.queue.sqs.TbAwsSqsQueueAttributes;
import com.vizzionnaire.server.queue.sqs.TbAwsSqsSettings;

@Configuration
public class RuleEngineTbQueueAdminFactory {

    @Autowired(required = false)
    private TbKafkaTopicConfigs kafkaTopicConfigs;
    @Autowired(required = false)
    private TbKafkaSettings kafkaSettings;

    @Autowired(required = false)
    private TbAwsSqsQueueAttributes awsSqsQueueAttributes;
    @Autowired(required = false)
    private TbAwsSqsSettings awsSqsSettings;

    @Autowired(required = false)
    private TbPubSubSubscriptionSettings pubSubSubscriptionSettings;
    @Autowired(required = false)
    private TbPubSubSettings pubSubSettings;

    @Autowired(required = false)
    private TbRabbitMqQueueArguments rabbitMqQueueArguments;
    @Autowired(required = false)
    private TbRabbitMqSettings rabbitMqSettings;

    @Autowired(required = false)
    private TbServiceBusQueueConfigs serviceBusQueueConfigs;
    @Autowired(required = false)
    private TbServiceBusSettings serviceBusSettings;

    @ConditionalOnExpression("'${queue.type:null}'=='kafka'")
    @Bean
    public TbQueueAdmin createKafkaAdmin() {
        return new TbKafkaAdmin(kafkaSettings, kafkaTopicConfigs.getRuleEngineConfigs());
    }

    @ConditionalOnExpression("'${queue.type:null}'=='aws-sqs'")
    @Bean
    public TbQueueAdmin createAwsSqsAdmin() {
        return new TbAwsSqsAdmin(awsSqsSettings, awsSqsQueueAttributes.getRuleEngineAttributes());
    }

    @ConditionalOnExpression("'${queue.type:null}'=='pubsub'")
    @Bean
    public TbQueueAdmin createPubSubAdmin() {
        return new TbPubSubAdmin(pubSubSettings, pubSubSubscriptionSettings.getRuleEngineSettings());
    }

    @ConditionalOnExpression("'${queue.type:null}'=='rabbitmq'")
    @Bean
    public TbQueueAdmin createRabbitMqAdmin() {
        return new TbRabbitMqAdmin(rabbitMqSettings, rabbitMqQueueArguments.getRuleEngineArgs());
    }

    @ConditionalOnExpression("'${queue.type:null}'=='service-bus'")
    @Bean
    public TbQueueAdmin createServiceBusAdmin() {
        return new TbServiceBusAdmin(serviceBusSettings, serviceBusQueueConfigs.getRuleEngineConfigs());
    }

    @ConditionalOnExpression("'${queue.type:null}'=='in-memory'")
    @Bean
    public TbQueueAdmin createInMemoryAdmin() {
        return new TbQueueAdmin() {

            @Override
            public void createTopicIfNotExists(String topic) {
            }

            @Override
            public void deleteTopic(String topic) {
            }

            @Override
            public void destroy() {
            }
        };
    }
}