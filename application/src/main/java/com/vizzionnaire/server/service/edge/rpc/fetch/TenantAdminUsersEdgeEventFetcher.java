package com.vizzionnaire.server.service.edge.rpc.fetch;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.user.UserService;

public class TenantAdminUsersEdgeEventFetcher extends BaseUsersEdgeEventFetcher {

    public TenantAdminUsersEdgeEventFetcher(UserService userService) {
        super(userService);
    }

    @Override
    protected PageData<User> findUsers(TenantId tenantId, PageLink pageLink) {
        return userService.findTenantAdmins(tenantId, pageLink);
    }
}
