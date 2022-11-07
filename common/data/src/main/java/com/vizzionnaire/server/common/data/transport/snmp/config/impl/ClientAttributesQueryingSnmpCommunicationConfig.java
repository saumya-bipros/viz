package com.vizzionnaire.server.common.data.transport.snmp.config.impl;

import com.vizzionnaire.server.common.data.transport.snmp.SnmpCommunicationSpec;
import com.vizzionnaire.server.common.data.transport.snmp.config.RepeatingQueryingSnmpCommunicationConfig;

public class ClientAttributesQueryingSnmpCommunicationConfig extends RepeatingQueryingSnmpCommunicationConfig {

    @Override
    public SnmpCommunicationSpec getSpec() {
        return SnmpCommunicationSpec.CLIENT_ATTRIBUTES_QUERYING;
    }

}
