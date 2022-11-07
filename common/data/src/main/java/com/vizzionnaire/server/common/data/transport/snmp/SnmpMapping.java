package com.vizzionnaire.server.common.data.transport.snmp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.kv.DataType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SnmpMapping {
    private String oid;
    private String key;
    private DataType dataType;

    private static final Pattern OID_PATTERN = Pattern.compile("^\\.?([0-2])((\\.0)|(\\.[1-9][0-9]*))*$");

    @JsonIgnore
    public boolean isValid() {
        return StringUtils.isNotEmpty(oid) && OID_PATTERN.matcher(oid).matches() && StringUtils.isNotBlank(key);
    }
}
