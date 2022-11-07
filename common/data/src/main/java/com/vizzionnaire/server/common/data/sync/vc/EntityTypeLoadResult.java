package com.vizzionnaire.server.common.data.sync.vc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.EntityType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntityTypeLoadResult implements Serializable {
    private static final long serialVersionUID = -8428039809651395241L;

    private EntityType entityType;
    private int created;
    private int updated;
    private int deleted;

    public EntityTypeLoadResult(EntityType entityType) {
        this.entityType = entityType;
    }
}
