package com.vizzionnaire.server.service.entitiy.dashboard;

import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.Dashboard;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DashboardId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.service.entitiy.SimpleTbEntityService;

import java.util.Set;

public interface TbDashboardService extends SimpleTbEntityService<Dashboard> {

    Dashboard assignDashboardToCustomer(Dashboard dashboard, Customer customer, User user) throws VizzionnaireException;

    Dashboard assignDashboardToPublicCustomer(Dashboard dashboard, User user) throws VizzionnaireException;

    Dashboard unassignDashboardFromPublicCustomer(Dashboard dashboard, User user) throws VizzionnaireException;

    Dashboard updateDashboardCustomers(Dashboard dashboard, Set<CustomerId> customerIds, User user) throws VizzionnaireException;

    Dashboard addDashboardCustomers(Dashboard dashboard, Set<CustomerId> customerIds, User user) throws VizzionnaireException;

    Dashboard removeDashboardCustomers(Dashboard dashboard, Set<CustomerId> customerIds, User user) throws VizzionnaireException;

    Dashboard asignDashboardToEdge(TenantId tenantId, DashboardId dashboardId, Edge edge, User user) throws VizzionnaireException;

    Dashboard unassignDashboardFromEdge(Dashboard dashboard, Edge edge, User user) throws VizzionnaireException;

    Dashboard unassignDashboardFromCustomer(Dashboard dashboard, Customer customer, User user) throws VizzionnaireException;

}
