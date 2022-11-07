package com.vizzionnaire.server.transport.snmp.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.queue.discovery.TbApplicationEventListener;
import com.vizzionnaire.server.queue.discovery.event.ServiceListChangedEvent;
import com.vizzionnaire.server.queue.util.TbSnmpTransportComponent;
import com.vizzionnaire.server.transport.snmp.service.SnmpTransportBalancingService;

@TbSnmpTransportComponent
@Component
@RequiredArgsConstructor
public class ServiceListChangedEventListener extends TbApplicationEventListener<ServiceListChangedEvent> {
    private final SnmpTransportBalancingService snmpTransportBalancingService;

    @Override
    protected void onTbApplicationEvent(ServiceListChangedEvent event) {
        snmpTransportBalancingService.onServiceListChanged(event);
    }
}
