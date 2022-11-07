package com.vizzionnaire.server.service.entitiy.dashboard;

import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.Dashboard;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DashboardId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.service.entitiy.SimpleTbEntityService;

import java.util.Set;

public interface TbDashboardService extends SimpleTbEntityService<Dashboard> {

    Dashboard assignDashboardToCustomer(Dashboard dashboard, Customer customer, User user) throws ThingsboardException;

    Dashboard assignDashboardToPublicCustomer(Dashboard dashboard, User user) throws ThingsboardException;

    Dashboard unassignDashboardFromPublicCustomer(Dashboard dashboard, User user) throws ThingsboardException;

    Dashboard updateDashboardCustomers(Dashboard dashboard, Set<CustomerId> customerIds, User user) throws ThingsboardException;

    Dashboard addDashboardCustomers(Dashboard dashboard, Set<CustomerId> customerIds, User user) throws ThingsboardException;

    Dashboard removeDashboardCustomers(Dashboard dashboard, Set<CustomerId> customerIds, User user) throws ThingsboardException;

    Dashboard asignDashboardToEdge(TenantId tenantId, DashboardId dashboardId, Edge edge, User user) throws ThingsboardException;

    Dashboard unassignDashboardFromEdge(Dashboard dashboard, Edge edge, User user) throws ThingsboardException;

    Dashboard unassignDashboardFromCustomer(Dashboard dashboard, Customer customer, User user) throws ThingsboardException;

}
