package com.vizzionnaire.server.queue;

import com.vizzionnaire.server.common.data.queue.Queue;

public interface TbQueueClusterService {
    void onQueueChange(Queue queue);

    void onQueueDelete(Queue queue);
}
