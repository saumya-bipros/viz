package com.vizzionnaire.server.service.state;

import lombok.Builder;
import lombok.Data;

import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;

/**
 * Created by ashvayka on 01.05.18.
 */
@Data
@Builder
class DeviceStateData {

    private final TenantId tenantId;
    private final CustomerId customerId;
    private final DeviceId deviceId;
    private final long deviceCreationTime;
    private TbMsgMetaData metaData;
    private final DeviceState state;
    
}
