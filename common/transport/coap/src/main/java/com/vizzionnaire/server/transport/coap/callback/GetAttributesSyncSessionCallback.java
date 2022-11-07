package com.vizzionnaire.server.transport.coap.callback;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.vizzionnaire.server.common.transport.adaptor.AdaptorException;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.transport.coap.client.TbCoapClientState;

@Slf4j
public class GetAttributesSyncSessionCallback extends AbstractSyncSessionCallback {

    public GetAttributesSyncSessionCallback(TbCoapClientState state, CoapExchange exchange, Request request) {
        super(state, exchange, request);
    }

    @Override
    public void onGetAttributesResponse(TransportProtos.GetAttributeResponseMsg msg) {
        try {
            respond(state.getAdaptor().convertToPublish(msg));
        } catch (AdaptorException e) {
            log.trace("[{}] Failed to reply due to error", state.getDeviceId(), e);
            exchange.respond(new Response(CoAP.ResponseCode.INTERNAL_SERVER_ERROR));
        }
    }

}
