package com.vizzionnaire.server.transport.coap;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.transport.TransportContext;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.transport.coap.adaptors.JsonCoapAdaptor;
import com.vizzionnaire.server.transport.coap.adaptors.ProtoCoapAdaptor;
import com.vizzionnaire.server.transport.coap.client.CoapClientContext;
import com.vizzionnaire.server.transport.coap.efento.adaptor.EfentoCoapAdaptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by ashvayka on 18.10.18.
 */
@Slf4j
@ConditionalOnExpression("'${service.type:null}'=='tb-transport' || ('${service.type:null}'=='monolith' && '${transport.api_enabled:true}'=='true' && '${transport.coap.enabled}'=='true')")
@Component
@Getter
public class CoapTransportContext extends TransportContext {

    @Value("${transport.sessions.report_timeout}")
    private long sessionReportTimeout;

    @Autowired
    private JsonCoapAdaptor jsonCoapAdaptor;

    @Autowired
    private ProtoCoapAdaptor protoCoapAdaptor;

    @Autowired
    private EfentoCoapAdaptor efentoCoapAdaptor;

    @Autowired
    private CoapClientContext clientContext;

    private final ConcurrentMap<Integer, TransportProtos.ToDeviceRpcRequestMsg> rpcAwaitingAck = new ConcurrentHashMap<>();

}
