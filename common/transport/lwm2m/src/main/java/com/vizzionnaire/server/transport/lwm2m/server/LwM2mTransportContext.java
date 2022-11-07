package com.vizzionnaire.server.transport.lwm2m.server;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.transport.TransportContext;
import com.vizzionnaire.server.queue.util.TbLwM2mTransportComponent;

@Component
@TbLwM2mTransportComponent
public class LwM2mTransportContext extends TransportContext {

    @Getter @Setter
    private LeshanServer server;

}
