package com.vizzionnaire.server.common.data.query;

import com.vizzionnaire.server.common.data.id.CustomerId;

import lombok.Data;

@Data
public class ApiUsageStateFilter implements EntityFilter {

    private CustomerId customerId;

    @Override
    public EntityFilterType getType() {
        return EntityFilterType.API_USAGE_STATE;
    }

}
