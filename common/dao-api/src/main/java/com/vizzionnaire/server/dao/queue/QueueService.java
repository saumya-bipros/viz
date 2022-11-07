package com.vizzionnaire.server.dao.queue;

import java.util.List;

import com.vizzionnaire.server.common.data.id.QueueId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.queue.Queue;

public interface QueueService {

    Queue saveQueue(Queue queue);

    void deleteQueue(TenantId tenantId, QueueId queueId);

    List<Queue> findQueuesByTenantId(TenantId tenantId);

    PageData<Queue> findQueuesByTenantId(TenantId tenantId, PageLink pageLink);

    List<Queue> findAllQueues();

    Queue findQueueById(TenantId tenantId, QueueId queueId);

    Queue findQueueByTenantIdAndName(TenantId tenantId, String name);

    Queue findQueueByTenantIdAndNameInternal(TenantId tenantId, String queueName);

    void deleteQueuesByTenantId(TenantId tenantId);
}