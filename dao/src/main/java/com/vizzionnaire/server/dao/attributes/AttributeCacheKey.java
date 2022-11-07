package com.vizzionnaire.server.dao.attributes;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.id.EntityId;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class AttributeCacheKey implements Serializable {
    private static final long serialVersionUID = 2013369077925351881L;

    private final String scope;
    private final EntityId entityId;
    private final String key;

    @Override
    public String toString() {
        return "{" + entityId + "}" + scope + "_" + key;
    }
}
