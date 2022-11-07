package com.vizzionnaire.server.service.apiusage;

import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.EntityType;

public class CustomerApiUsageState extends BaseApiUsageState {
    public CustomerApiUsageState(ApiUsageState apiUsageState) {
        super(apiUsageState);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.CUSTOMER;
    }
}
