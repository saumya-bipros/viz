package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.vizzionnaire.server.common.data.event.ErrorEvent;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.model.BaseEntity;

import static com.vizzionnaire.server.dao.model.ModelConstants.ERROR_EVENT_TABLE_NAME;
import static com.vizzionnaire.server.dao.model.ModelConstants.EVENT_ERROR_COLUMN_NAME;
import static com.vizzionnaire.server.dao.model.ModelConstants.EVENT_METHOD_COLUMN_NAME;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = ERROR_EVENT_TABLE_NAME)
@NoArgsConstructor
public class ErrorEventEntity extends EventEntity<ErrorEvent> implements BaseEntity<ErrorEvent> {

    @Column(name = EVENT_METHOD_COLUMN_NAME)
    private String method;
    @Column(name = EVENT_ERROR_COLUMN_NAME)
    private String error;

    public ErrorEventEntity(ErrorEvent event) {
        super(event);
        this.method = event.getMethod();
        this.error = event.getError();
    }

    @Override
    public ErrorEvent toData() {
        return ErrorEvent.builder()
                .tenantId(TenantId.fromUUID(tenantId))
                .entityId(entityId)
                .serviceId(serviceId)
                .id(id)
                .ts(ts)
                .method(method)
                .error(error)
                .build();
    }

}
