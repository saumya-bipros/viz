package com.vizzionnaire.server.queue.usagestats;

import com.vizzionnaire.server.common.data.ApiUsageRecordKey;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;

public interface TbApiUsageClient {

    void report(TenantId tenantId, CustomerId customerId, ApiUsageRecordKey key, long value);

    void report(TenantId tenantId, CustomerId customerId, ApiUsageRecordKey key);

}
