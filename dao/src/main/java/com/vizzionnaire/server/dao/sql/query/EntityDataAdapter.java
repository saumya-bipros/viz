package com.vizzionnaire.server.dao.sql.query;

import org.apache.commons.lang3.math.NumberUtils;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.EntityIdFactory;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.query.EntityData;
import com.vizzionnaire.server.common.data.query.EntityDataPageLink;
import com.vizzionnaire.server.common.data.query.EntityKey;
import com.vizzionnaire.server.common.data.query.EntityKeyType;
import com.vizzionnaire.server.common.data.query.TsValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntityDataAdapter {

    public static PageData<EntityData> createEntityData(EntityDataPageLink pageLink,
                                                        List<EntityKeyMapping> selectionMapping,
                                                        List<Map<String, Object>> rows,
                                                        int totalElements) {
        int totalPages = pageLink.getPageSize() > 0 ? (int) Math.ceil((float) totalElements / pageLink.getPageSize()) : 1;
        int startIndex = pageLink.getPageSize() * pageLink.getPage();
        boolean hasNext = pageLink.getPageSize() > 0 && totalElements > startIndex + rows.size();
        List<EntityData> entitiesData = convertListToEntityData(rows, selectionMapping);
        return new PageData<>(entitiesData, totalPages, totalElements, hasNext);
    }

    private static List<EntityData> convertListToEntityData(List<Map<String, Object>> result, List<EntityKeyMapping> selectionMapping) {
        return result.stream().map(row -> toEntityData(row, selectionMapping)).collect(Collectors.toList());
    }

    private static EntityData toEntityData(Map<String, Object> row, List<EntityKeyMapping> selectionMapping) {
        UUID id = (UUID)row.get("id");
        EntityType entityType = EntityType.valueOf((String) row.get("entity_type"));
        EntityId entityId = EntityIdFactory.getByTypeAndUuid(entityType, id);
        Map<EntityKeyType, Map<String, TsValue>> latest = new HashMap<>();
        Map<String, TsValue[]> timeseries = new HashMap<>();
        EntityData entityData = new EntityData(entityId, latest, timeseries);
        for (EntityKeyMapping mapping : selectionMapping) {
            if (!mapping.isIgnore()) {
                EntityKey entityKey = mapping.getEntityKey();
                Object value = row.get(mapping.getValueAlias());
                String strValue;
                long ts;
                if (entityKey.getType().equals(EntityKeyType.ENTITY_FIELD)) {
                    strValue = value != null ? value.toString() : "";
                    ts = System.currentTimeMillis();
                } else {
                    strValue = convertValue(value);
                    Object tsObject = row.get(mapping.getTsAlias());
                    ts = tsObject != null ? Long.parseLong(tsObject.toString()) : 0;
                }
                TsValue tsValue = new TsValue(ts, strValue);
                latest.computeIfAbsent(entityKey.getType(), entityKeyType -> new HashMap<>()).put(entityKey.getKey(), tsValue);
            }
        }
        return entityData;
    }

    static String convertValue(Object value) {
        if (value != null) {
            String strVal = value.toString();
            // check number
            if (NumberUtils.isParsable(strVal)) {
                if (strVal.startsWith("0") && !strVal.startsWith("0.")) {
                    return strVal;
                }
                try {
                    long longVal = Long.parseLong(strVal);
                    return Long.toString(longVal);
                } catch (NumberFormatException ignored) {
                }
                try {
                    double dblVal = Double.parseDouble(strVal);
                    if (!Double.isInfinite(dblVal)) {
                        return Double.toString(dblVal);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            return strVal;
        } else {
            return "";
        }
    }

}
