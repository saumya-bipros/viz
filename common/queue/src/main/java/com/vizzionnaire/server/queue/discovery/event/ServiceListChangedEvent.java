package com.vizzionnaire.server.queue.discovery.event;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

import com.vizzionnaire.server.gen.transport.TransportProtos.ServiceInfo;

@Getter
@ToString
public class ServiceListChangedEvent extends TbApplicationEvent {
    private final List<ServiceInfo> otherServices;
    private final ServiceInfo currentService;

    public ServiceListChangedEvent(List<ServiceInfo> otherServices, ServiceInfo currentService) {
        super(otherServices);
        this.otherServices = otherServices;
        this.currentService = currentService;
    }
}
