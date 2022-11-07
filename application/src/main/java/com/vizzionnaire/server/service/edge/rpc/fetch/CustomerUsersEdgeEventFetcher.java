package com.vizzionnaire.server.service.edge.rpc.fetch;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.user.UserService;

public class CustomerUsersEdgeEventFetcher extends BaseUsersEdgeEventFetcher {

    private final CustomerId customerId;

    public CustomerUsersEdgeEventFetcher(UserService userService, CustomerId customerId) {
        super(userService);
        this.customerId = customerId;
    }

    @Override
    protected PageData<User> findUsers(TenantId tenantId, PageLink pageLink) {
        return userService.findCustomerUsers(tenantId, customerId, pageLink);
    }

}
