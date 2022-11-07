package com.vizzionnaire.server.service.entitiy.queue;

import java.util.List;

import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.QueueId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.queue.Queue;

public interface TbQueueService {

    Queue saveQueue(Queue queue);

    void deleteQueue(TenantId tenantId, QueueId queueId);

    void deleteQueueByQueueName(TenantId tenantId, String queueName);

    void updateQueuesByTenants(List<TenantId> tenantIds, TenantProfile newTenantProfile, TenantProfile oldTenantProfile);
}
