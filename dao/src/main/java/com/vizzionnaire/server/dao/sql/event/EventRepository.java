package com.vizzionnaire.server.dao.sql.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.EventInfo;
import com.vizzionnaire.server.common.data.event.Event;
import com.vizzionnaire.server.dao.model.sql.EventEntity;

import java.util.List;
import java.util.UUID;

public interface EventRepository<T extends EventEntity<V>, V extends Event> {

    List<T> findLatestEvents(UUID tenantId, UUID entityId, int limit);

    Page<T> findEvents(UUID tenantId, UUID entityId, Long startTime, Long endTime, Pageable pageable);

    void removeEvents(UUID tenantId, UUID entityId, Long startTime, Long endTime);

}
