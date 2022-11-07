package com.vizzionnaire.server.queue.provider;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToCoreMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToRuleEngineNotificationMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToTransportMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import com.vizzionnaire.server.queue.TbQueueProducer;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;

import javax.annotation.PostConstruct;

@Service
@ConditionalOnExpression("'${service.type:null}'=='tb-vc-executor'")
public class TbVersionControlProducerProvider implements TbQueueProducerProvider {

    private final TbVersionControlQueueFactory tbQueueProvider;
    private TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> toTbCoreNotifications;
    private TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> toUsageStats;

    public TbVersionControlProducerProvider(TbVersionControlQueueFactory tbQueueProvider) {
        this.tbQueueProvider = tbQueueProvider;
    }

    @PostConstruct
    public void init() {
        this.toTbCoreNotifications = tbQueueProvider.createTbCoreNotificationsMsgProducer();
        this.toUsageStats = tbQueueProvider.createToUsageStatsServiceMsgProducer();
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToTransportMsg>> getTransportNotificationsMsgProducer() {
        throw new RuntimeException("Not Implemented! Should not be used by Version Control Service!");
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> getRuleEngineMsgProducer() {
         throw new RuntimeException("Not Implemented! Should not be used by Version Control Service!");
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> getTbCoreMsgProducer() {
        throw new RuntimeException("Not Implemented! Should not be used by Version Control Service!");
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> getRuleEngineNotificationsMsgProducer() {
        throw new RuntimeException("Not Implemented! Should not be used by Version Control Service!");
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> getTbCoreNotificationsMsgProducer() {
        return toTbCoreNotifications;
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<TransportProtos.ToVersionControlServiceMsg>> getTbVersionControlMsgProducer() {
        throw new RuntimeException("Not Implemented! Should not be used by Version Control Service!");
    }

    @Override
    public TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> getTbUsageStatsMsgProducer() {
        return toUsageStats;
    }
}
