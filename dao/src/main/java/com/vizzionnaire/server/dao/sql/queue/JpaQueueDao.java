package com.vizzionnaire.server.dao.sql.queue;

import com.google.common.collect.Lists;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.queue.Queue;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.QueueEntity;
import com.vizzionnaire.server.dao.queue.QueueDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class JpaQueueDao extends JpaAbstractDao<QueueEntity, Queue> implements QueueDao {

    @Autowired
    private QueueRepository queueRepository;

    @Override
    protected Class<QueueEntity> getEntityClass() {
        return QueueEntity.class;
    }

    @Override
    protected JpaRepository<QueueEntity, UUID> getRepository() {
        return queueRepository;
    }

    @Override
    public Queue findQueueByTenantIdAndTopic(TenantId tenantId, String topic) {
        return DaoUtil.getData(queueRepository.findByTenantIdAndTopic(tenantId.getId(), topic));
    }

    @Override
    public Queue findQueueByTenantIdAndName(TenantId tenantId, String name) {
        return DaoUtil.getData(queueRepository.findByTenantIdAndName(tenantId.getId(), name));
    }

    @Override
    public List<Queue> findAllByTenantId(TenantId tenantId) {
        List<QueueEntity> entities = queueRepository.findByTenantId(tenantId.getId());
        return DaoUtil.convertDataList(entities);
    }

    @Override
    public List<Queue> findAllMainQueues() {
        List<QueueEntity> entities = Lists.newArrayList(queueRepository.findAllByName("Main"));
        return DaoUtil.convertDataList(entities);
    }

    @Override
    public List<Queue> findAllQueues() {
        List<QueueEntity> entities = Lists.newArrayList(queueRepository.findAll());
        return DaoUtil.convertDataList(entities);
    }

    @Override
    public PageData<Queue> findQueuesByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(queueRepository
                .findByTenantId(tenantId.getId(), Objects.toString(pageLink.getTextSearch(), ""), DaoUtil.toPageable(pageLink)));
    }
}