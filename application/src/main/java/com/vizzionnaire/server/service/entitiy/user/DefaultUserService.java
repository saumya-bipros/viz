package com.vizzionnaire.server.service.entitiy.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.vizzionnaire.rule.engine.api.MailService;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.UserCredentials;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.AbstractTbEntityService;
import com.vizzionnaire.server.service.security.system.SystemSecurityService;

import javax.servlet.http.HttpServletRequest;

import static com.vizzionnaire.server.controller.UserController.ACTIVATE_URL_PATTERN;

import java.util.List;

@Service
@TbCoreComponent
@AllArgsConstructor
@Slf4j
public class DefaultUserService extends AbstractTbEntityService implements TbUserService {

    private final UserService userService;
    private final MailService mailService;
    private final SystemSecurityService systemSecurityService;

    @Override
    public User save(TenantId tenantId, CustomerId customerId, User tbUser, boolean sendActivationMail,
                     HttpServletRequest request, User user) throws ThingsboardException {
        ActionType actionType = tbUser.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        try {
            boolean sendEmail = tbUser.getId() == null && sendActivationMail;
            User savedUser = checkNotNull(userService.saveUser(tbUser));
            if (sendEmail) {
                UserCredentials userCredentials = userService.findUserCredentialsByUserId(tenantId, savedUser.getId());
                String baseUrl = systemSecurityService.getBaseUrl(tenantId, customerId, request);
                String activateUrl = String.format(ACTIVATE_URL_PATTERN, baseUrl,
                        userCredentials.getActivateToken());
                String email = savedUser.getEmail();
                try {
                    mailService.sendActivationEmail(activateUrl, email);
                } catch (ThingsboardException e) {
                    userService.deleteUser(tenantId, savedUser.getId());
                    throw e;
                }
            }
            notificationEntityService.notifyCreateOrUpdateOrDelete(tenantId, customerId, savedUser.getId(),
                    savedUser, user, actionType, true, null);
            return savedUser;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.USER), tbUser, actionType, user, e);
            throw e;
        }
    }

    @Override
    public void delete(TenantId tenantId, CustomerId customerId, User tbUser, User user) throws ThingsboardException {
        UserId userId = tbUser.getId();

        try {
            List<EdgeId> relatedEdgeIds = findRelatedEdgeIds(tenantId, userId);
            userService.deleteUser(tenantId, userId);
            notificationEntityService.notifyDeleteEntity(tenantId, userId, tbUser, customerId,
                    ActionType.DELETED, relatedEdgeIds, user, userId.toString());
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.USER),
                    ActionType.DELETED, user, e, userId.toString());
            throw e;
        }
    }
}
