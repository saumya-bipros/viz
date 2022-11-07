package com.vizzionnaire.server.dao.queue;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.queue.Queue;
import com.vizzionnaire.server.dao.Dao;

import java.util.List;

public interface QueueDao extends Dao<Queue> {
    Queue findQueueByTenantIdAndTopic(TenantId tenantId, String topic);

    Queue findQueueByTenantIdAndName(TenantId tenantId, String name);

    List<Queue> findAllMainQueues();

    List<Queue> findAllQueues();

    List<Queue> findAllByTenantId(TenantId tenantId);

    PageData<Queue> findQueuesByTenantId(TenantId tenantId, PageLink pageLink);
}