package com.vizzionnaire.server.transport.lwm2m.server.rpc;

import org.eclipse.leshan.core.ResponseCode;

import com.vizzionnaire.server.common.transport.TransportService;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;
import com.vizzionnaire.server.transport.lwm2m.server.downlink.DownlinkRequestCallback;
import com.vizzionnaire.server.transport.lwm2m.server.downlink.TbLwM2MCancelObserveRequest;

public class RpcCancelObserveCallback extends RpcDownlinkRequestCallbackProxy<TbLwM2MCancelObserveRequest, Integer> {

    public RpcCancelObserveCallback(TransportService transportService, LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, DownlinkRequestCallback<TbLwM2MCancelObserveRequest, Integer> callback) {
        super(transportService, client, requestMsg, callback);
    }

    @Override
    protected void sendRpcReplyOnSuccess(Integer response) {
        reply(LwM2MRpcResponseBody.builder().result(ResponseCode.CONTENT.getName()).value(response.toString()).build());
    }
}
