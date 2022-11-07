package com.vizzionnaire.server.transport.coap.callback;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.vizzionnaire.server.common.transport.adaptor.AdaptorException;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.transport.coap.client.TbCoapClientState;

@Slf4j
public class ToServerRpcSyncSessionCallback extends AbstractSyncSessionCallback {

    public ToServerRpcSyncSessionCallback(TbCoapClientState state, CoapExchange exchange, Request request) {
        super(state, exchange, request);
    }

    @Override
    public void onToServerRpcResponse(TransportProtos.ToServerRpcResponseMsg toServerResponse) {
        try {
            respond(state.getAdaptor().convertToPublish(toServerResponse));
        } catch (AdaptorException e) {
            log.trace("Failed to reply due to error", e);
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
