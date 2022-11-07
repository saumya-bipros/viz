package com.vizzionnaire.server.transport.snmp.event;

import com.vizzionnaire.server.queue.discovery.event.TbApplicationEvent;

public class SnmpTransportListChangedEvent extends TbApplicationEvent {
    public SnmpTransportListChangedEvent() {
        super(new Object());
    }
}
