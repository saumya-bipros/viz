package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.user.UserService;

@Slf4j
@AllArgsConstructor
public abstract class BaseUsersEdgeEventFetcher extends BasePageableEdgeEventFetcher<User> {

    protected final UserService userService;

    @Override
    PageData<User> fetchPageData(TenantId tenantId, Edge edge, PageLink pageLink) {
        return findUsers(tenantId, pageLink);
    }

    @Override
    EdgeEvent constructEdgeEvent(TenantId tenantId, Edge edge, User user) {
        return EdgeUtils.constructEdgeEvent(tenantId, edge.getId(), EdgeEventType.USER,
                EdgeEventActionType.ADDED, user.getId(), null);
    }

    protected abstract PageData<User> findUsers(TenantId tenantId, PageLink pageLink);
}
