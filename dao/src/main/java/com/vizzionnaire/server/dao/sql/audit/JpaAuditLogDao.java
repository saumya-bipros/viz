package com.vizzionnaire.server.dao.sql.audit;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.audit.AuditLog;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.TimePageLink;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.audit.AuditLogDao;
import com.vizzionnaire.server.dao.model.sql.AuditLogEntity;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class JpaAuditLogDao extends JpaAbstractDao<AuditLogEntity, AuditLog> implements AuditLogDao {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    protected Class<AuditLogEntity> getEntityClass() {
        return AuditLogEntity.class;
    }

    @Override
    protected JpaRepository<AuditLogEntity, UUID> getRepository() {
        return auditLogRepository;
    }

    @Override
    public ListenableFuture<Void> saveByTenantId(AuditLog auditLog) {
        return service.submit(() -> {
            save(auditLog.getTenantId(), auditLog);
            return null;
        });
    }

    @Override
    public PageData<AuditLog> findAuditLogsByTenantIdAndEntityId(UUID tenantId, EntityId entityId, List<ActionType> actionTypes, TimePageLink pageLink) {
        return DaoUtil.toPageData(
                auditLogRepository
                        .findAuditLogsByTenantIdAndEntityId(
                                tenantId,
                                entityId.getEntityType(),
                                entityId.getId(),
                                Objects.toString(pageLink.getTextSearch(), ""),
                                pageLink.getStartTime(),
                                pageLink.getEndTime(),
                                actionTypes,
                                DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<AuditLog> findAuditLogsByTenantIdAndCustomerId(UUID tenantId, CustomerId customerId, List<ActionType> actionTypes, TimePageLink pageLink) {
        return DaoUtil.toPageData(
                auditLogRepository
                        .findAuditLogsByTenantIdAndCustomerId(
                                tenantId,
                                customerId.getId(),
                                Objects.toString(pageLink.getTextSearch(), ""),
                                pageLink.getStartTime(),
                                pageLink.getEndTime(),
                                actionTypes,
                                DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<AuditLog> findAuditLogsByTenantIdAndUserId(UUID tenantId, UserId userId, List<ActionType> actionTypes, TimePageLink pageLink) {
        return DaoUtil.toPageData(
                auditLogRepository
                        .findAuditLogsByTenantIdAndUserId(
                                tenantId,
                                userId.getId(),
                                Objects.toString(pageLink.getTextSearch(), ""),
                                pageLink.getStartTime(),
                                pageLink.getEndTime(),
                                actionTypes,
                                DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<AuditLog> findAuditLogsByTenantId(UUID tenantId, List<ActionType> actionTypes, TimePageLink pageLink) {
        return DaoUtil.toPageData(
                auditLogRepository.findByTenantId(
                        tenantId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        pageLink.getStartTime(),
                        pageLink.getEndTime(),
                        actionTypes,
                        DaoUtil.toPageable(pageLink)));
    }
}
