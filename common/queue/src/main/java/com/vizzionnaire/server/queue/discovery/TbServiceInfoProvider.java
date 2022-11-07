package com.vizzionnaire.server.queue.discovery;

import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.gen.transport.TransportProtos.ServiceInfo;

public interface TbServiceInfoProvider {

    String getServiceId();

    String getServiceType();

    ServiceInfo getServiceInfo();

    boolean isService(ServiceType serviceType);

}
