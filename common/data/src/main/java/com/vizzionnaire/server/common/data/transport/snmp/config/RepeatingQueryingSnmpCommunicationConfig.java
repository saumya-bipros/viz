package com.vizzionnaire.server.common.data.transport.snmp.config;

import com.vizzionnaire.server.common.data.transport.snmp.SnmpMethod;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class RepeatingQueryingSnmpCommunicationConfig extends MultipleMappingsSnmpCommunicationConfig {
    private Long queryingFrequencyMs;

    @Override
    public SnmpMethod getMethod() {
        return SnmpMethod.GET;
    }

    @Override
    public boolean isValid() {
        return queryingFrequencyMs != null && queryingFrequencyMs > 0 && super.isValid();
    }
}
