package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.vizzionnaire.server.common.data.event.StatisticsEvent;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.model.BaseEntity;

import static com.vizzionnaire.server.dao.model.ModelConstants.EVENT_ERRORS_OCCURRED_COLUMN_NAME;
import static com.vizzionnaire.server.dao.model.ModelConstants.EVENT_MESSAGES_PROCESSED_COLUMN_NAME;
import static com.vizzionnaire.server.dao.model.ModelConstants.STATS_EVENT_TABLE_NAME;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = STATS_EVENT_TABLE_NAME)
@NoArgsConstructor
public class StatisticsEventEntity extends EventEntity<StatisticsEvent> implements BaseEntity<StatisticsEvent> {

    @Column(name = EVENT_MESSAGES_PROCESSED_COLUMN_NAME)
    private long messagesProcessed;
    @Column(name = EVENT_ERRORS_OCCURRED_COLUMN_NAME)
    private long errorsOccurred;

    public StatisticsEventEntity(StatisticsEvent event) {
        super(event);
        this.messagesProcessed = event.getMessagesProcessed();
        this.errorsOccurred = event.getErrorsOccurred();
    }

    @Override
    public StatisticsEvent toData() {
        return StatisticsEvent.builder()
                .tenantId(TenantId.fromUUID(tenantId))
                .entityId(entityId)
                .serviceId(serviceId)
                .id(id)
                .ts(ts)
                .messagesProcessed(messagesProcessed)
                .errorsOccurred(errorsOccurred)
                .build();
    }

}
