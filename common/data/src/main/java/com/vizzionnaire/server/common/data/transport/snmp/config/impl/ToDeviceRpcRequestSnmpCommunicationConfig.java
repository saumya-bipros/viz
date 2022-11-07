package com.vizzionnaire.server.common.data.transport.snmp.config.impl;

import com.vizzionnaire.server.common.data.transport.snmp.SnmpCommunicationSpec;
import com.vizzionnaire.server.common.data.transport.snmp.config.MultipleMappingsSnmpCommunicationConfig;

public class ToDeviceRpcRequestSnmpCommunicationConfig extends MultipleMappingsSnmpCommunicationConfig {
    @Override
    public SnmpCommunicationSpec getSpec() {
        return SnmpCommunicationSpec.TO_DEVICE_RPC_REQUEST;
    }
}
