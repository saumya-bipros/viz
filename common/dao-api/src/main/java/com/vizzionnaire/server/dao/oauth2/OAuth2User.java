package com.vizzionnaire.server.dao.oauth2;

import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Data;

@Data
public class OAuth2User {
    private String tenantName;
    private TenantId tenantId;
    private String customerName;
    private CustomerId customerId;
    private String email;
    private String firstName;
    private String lastName;
    private boolean alwaysFullScreen;
    private String defaultDashboardName;
}
