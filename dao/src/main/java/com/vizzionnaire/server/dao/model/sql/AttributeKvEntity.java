package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;

import com.vizzionnaire.server.common.data.kv.AttributeKvEntry;
import com.vizzionnaire.server.common.data.kv.BaseAttributeKvEntry;
import com.vizzionnaire.server.common.data.kv.BooleanDataEntry;
import com.vizzionnaire.server.common.data.kv.DoubleDataEntry;
import com.vizzionnaire.server.common.data.kv.JsonDataEntry;
import com.vizzionnaire.server.common.data.kv.KvEntry;
import com.vizzionnaire.server.common.data.kv.LongDataEntry;
import com.vizzionnaire.server.common.data.kv.StringDataEntry;
import com.vizzionnaire.server.dao.model.ToData;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import static com.vizzionnaire.server.dao.model.ModelConstants.BOOLEAN_VALUE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.DOUBLE_VALUE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.JSON_VALUE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.LAST_UPDATE_TS_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.LONG_VALUE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.STRING_VALUE_COLUMN;

import java.io.Serializable;

@Data
@Entity
@Table(name = "attribute_kv")
public class AttributeKvEntity implements ToData<AttributeKvEntry>, Serializable {

    @EmbeddedId
    private AttributeKvCompositeKey id;

    @Column(name = BOOLEAN_VALUE_COLUMN)
    private Boolean booleanValue;

    @Column(name = STRING_VALUE_COLUMN)
    private String strValue;

    @Column(name = LONG_VALUE_COLUMN)
    private Long longValue;

    @Column(name = DOUBLE_VALUE_COLUMN)
    private Double doubleValue;

    @Column(name = JSON_VALUE_COLUMN)
    private String jsonValue;

    @Column(name = LAST_UPDATE_TS_COLUMN)
    private Long lastUpdateTs;

    @Override
    public AttributeKvEntry toData() {
        KvEntry kvEntry = null;
        if (strValue != null) {
            kvEntry = new StringDataEntry(id.getAttributeKey(), strValue);
        } else if (booleanValue != null) {
            kvEntry = new BooleanDataEntry(id.getAttributeKey(), booleanValue);
        } else if (doubleValue != null) {
            kvEntry = new DoubleDataEntry(id.getAttributeKey(), doubleValue);
        } else if (longValue != null) {
            kvEntry = new LongDataEntry(id.getAttributeKey(), longValue);
        } else if (jsonValue != null) {
            kvEntry = new JsonDataEntry(id.getAttributeKey(), jsonValue);
        }

        return new BaseAttributeKvEntry(kvEntry, lastUpdateTs);
    }
}
