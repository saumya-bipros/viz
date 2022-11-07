package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;

import com.vizzionnaire.server.common.data.kv.BasicTsKvEntry;
import com.vizzionnaire.server.common.data.kv.BooleanDataEntry;
import com.vizzionnaire.server.common.data.kv.DoubleDataEntry;
import com.vizzionnaire.server.common.data.kv.JsonDataEntry;
import com.vizzionnaire.server.common.data.kv.KvEntry;
import com.vizzionnaire.server.common.data.kv.LongDataEntry;
import com.vizzionnaire.server.common.data.kv.StringDataEntry;
import com.vizzionnaire.server.common.data.kv.TsKvEntry;
import com.vizzionnaire.server.dao.model.ToData;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import static com.vizzionnaire.server.dao.model.ModelConstants.BOOLEAN_VALUE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.DOUBLE_VALUE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.ENTITY_ID_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.JSON_VALUE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.KEY_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.LONG_VALUE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.STRING_VALUE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.TS_COLUMN;

import java.util.UUID;

@Data
@MappedSuperclass
public abstract class AbstractTsKvEntity implements ToData<TsKvEntry> {

    protected static final String SUM = "SUM";
    protected static final String AVG = "AVG";
    protected static final String MIN = "MIN";
    protected static final String MAX = "MAX";

    @Id
    @Column(name = ENTITY_ID_COLUMN, columnDefinition = "uuid")
    protected UUID entityId;

    @Id
    @Column(name = KEY_COLUMN)
    protected int key;

    @Id
    @Column(name = TS_COLUMN)
    protected Long ts;

    @Column(name = BOOLEAN_VALUE_COLUMN)
    protected Boolean booleanValue;

    @Column(name = STRING_VALUE_COLUMN)
    protected String strValue;

    @Column(name = LONG_VALUE_COLUMN)
    protected Long longValue;

    @Column(name = DOUBLE_VALUE_COLUMN)
    protected Double doubleValue;

    @Column(name = JSON_VALUE_COLUMN)
    protected String jsonValue;

    @Transient
    protected String strKey;

    public abstract boolean isNotEmpty();

    protected static boolean isAllNull(Object... args) {
        for (Object arg : args) {
            if (arg != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TsKvEntry toData() {
        KvEntry kvEntry = null;
        if (strValue != null) {
            kvEntry = new StringDataEntry(strKey, strValue);
        } else if (longValue != null) {
            kvEntry = new LongDataEntry(strKey, longValue);
        } else if (doubleValue != null) {
            kvEntry = new DoubleDataEntry(strKey, doubleValue);
        } else if (booleanValue != null) {
            kvEntry = new BooleanDataEntry(strKey, booleanValue);
        } else if (jsonValue != null) {
            kvEntry = new JsonDataEntry(strKey, jsonValue);
        }
        return new BasicTsKvEntry(ts, kvEntry);
    }

}