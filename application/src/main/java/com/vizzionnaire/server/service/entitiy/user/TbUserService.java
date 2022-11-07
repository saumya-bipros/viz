package com.vizzionnaire.server.service.entitiy.user;

import javax.servlet.http.HttpServletRequest;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;

public interface TbUserService {
    User save(TenantId tenantId, CustomerId customerId, User tbUser, boolean sendActivationMail, HttpServletRequest request, User user) throws VizzionnaireException;

    void delete(TenantId tenantId, CustomerId customerId, User tbUser, User user) throws VizzionnaireException;
}
