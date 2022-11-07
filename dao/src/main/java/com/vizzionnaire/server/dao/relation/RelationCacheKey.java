package com.vizzionnaire.server.dao.relation;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;
import com.vizzionnaire.server.common.data.relation.RelationTypeGroup;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@Builder
public class RelationCacheKey implements Serializable {

    private static final long serialVersionUID = 3911151843961657570L;

    private final EntityId from;
    private final EntityId to;
    private final String type;
    private final RelationTypeGroup typeGroup;
    private final EntitySearchDirection direction;

    public RelationCacheKey(EntityId from, EntityId to, String type, RelationTypeGroup typeGroup) {
        this(from, to, type, typeGroup, null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = add(sb, true, from);
        first = add(sb, first, to);
        first = add(sb, first, type);
        first = add(sb, first, typeGroup);
        add(sb, first, direction);
        return sb.toString();
    }

    private boolean add(StringBuilder sb, boolean first, Object param) {
        if (param != null) {
            if (!first) {
                sb.append("_");
            }
            first = false;
            sb.append(param);
        }
        return first;
    }

}
