package com.vizzionnaire.server.common.data.tenant.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.server.common.data.queue.ProcessingStrategy;
import com.vizzionnaire.server.common.data.queue.SubmitStrategy;

import lombok.Data;

@Data
public class TenantProfileQueueConfiguration {
    private String name;
    private String topic;
    private int pollInterval;
    private int partitions;
    private boolean consumerPerPartition;
    private long packProcessingTimeout;
    private SubmitStrategy submitStrategy;
    private ProcessingStrategy processingStrategy;
    private JsonNode additionalInfo;
}
