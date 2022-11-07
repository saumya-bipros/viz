package com.vizzionnaire.server.transport.snmp.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.queue.discovery.TbApplicationEventListener;
import com.vizzionnaire.server.queue.util.TbSnmpTransportComponent;
import com.vizzionnaire.server.transport.snmp.SnmpTransportContext;

@TbSnmpTransportComponent
@Component
@RequiredArgsConstructor
public class SnmpTransportListChangedEventListener extends TbApplicationEventListener<SnmpTransportListChangedEvent> {
    private final SnmpTransportContext snmpTransportContext;

    @Override
    protected void onTbApplicationEvent(SnmpTransportListChangedEvent event) {
        snmpTransportContext.onSnmpTransportListChanged();
    }
}
