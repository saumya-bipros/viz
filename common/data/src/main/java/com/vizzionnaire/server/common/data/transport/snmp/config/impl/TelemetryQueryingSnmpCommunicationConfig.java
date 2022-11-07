package com.vizzionnaire.server.common.data.transport.snmp.config.impl;

import com.vizzionnaire.server.common.data.transport.snmp.SnmpCommunicationSpec;
import com.vizzionnaire.server.common.data.transport.snmp.config.RepeatingQueryingSnmpCommunicationConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TelemetryQueryingSnmpCommunicationConfig extends RepeatingQueryingSnmpCommunicationConfig {

    @Override
    public SnmpCommunicationSpec getSpec() {
        return SnmpCommunicationSpec.TELEMETRY_QUERYING;
    }

}
