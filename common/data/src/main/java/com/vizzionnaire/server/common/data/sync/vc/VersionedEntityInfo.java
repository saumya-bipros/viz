package com.vizzionnaire.server.common.data.sync.vc;

import com.vizzionnaire.server.common.data.id.EntityId;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VersionedEntityInfo {
    private EntityId externalId;

    public VersionedEntityInfo(EntityId externalId) {
        this.externalId = externalId;
    }
}
