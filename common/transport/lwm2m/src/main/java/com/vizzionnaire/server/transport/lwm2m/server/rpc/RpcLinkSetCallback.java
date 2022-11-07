package com.vizzionnaire.server.transport.lwm2m.server.rpc;

import org.eclipse.leshan.core.ResponseCode;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.transport.TransportService;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;
import com.vizzionnaire.server.transport.lwm2m.server.downlink.DownlinkRequestCallback;

public class RpcLinkSetCallback<R, T> extends RpcDownlinkRequestCallbackProxy<R, T> {

    public RpcLinkSetCallback(TransportService transportService, LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, DownlinkRequestCallback<R, T> callback) {
        super(transportService, client, requestMsg, callback);
    }

    @Override
    protected void sendRpcReplyOnSuccess(T response) {
        reply(LwM2MRpcResponseBody.builder().result(ResponseCode.CONTENT.getName()).value(JacksonUtil.toString(response)).build());
    }

}
