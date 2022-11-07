package com.vizzionnaire.server.service.apiusage;

import org.springframework.context.ApplicationListener;

import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.TenantProfileId;
import com.vizzionnaire.server.common.msg.queue.TbCallback;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.discovery.event.PartitionChangeEvent;

public interface TbApiUsageStateService extends ApplicationListener<PartitionChangeEvent> {

    void process(TbProtoQueueMsg<ToUsageStatsServiceMsg> msg, TbCallback callback);

    ApiUsageState getApiUsageState(TenantId tenantId);

    void onTenantProfileUpdate(TenantProfileId tenantProfileId);

    void onTenantUpdate(TenantId tenantId);

    void onTenantDelete(TenantId tenantId);

    void onCustomerDelete(CustomerId customerId);

    void onApiUsageStateUpdate(TenantId tenantId);
}
