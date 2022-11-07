package com.vizzionnaire.server.transport.lwm2m.server.rpc;

import org.eclipse.leshan.core.request.LwM2mRequest;
import org.eclipse.leshan.core.response.CreateResponse;

import com.vizzionnaire.server.common.transport.TransportService;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;
import com.vizzionnaire.server.transport.lwm2m.server.downlink.DownlinkRequestCallback;

import java.util.Optional;

public class RpcCreateResponseCallback<R extends LwM2mRequest<T>, T extends CreateResponse> extends RpcLwM2MDownlinkCallback<R, T> {

    public RpcCreateResponseCallback(TransportService transportService, LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, DownlinkRequestCallback<R, T> callback) {
        super(transportService, client, requestMsg, callback);
    }

    @Override
    protected Optional<String> serializeSuccessfulResponse(T response) {
        String value = response.getLocation() != null ? "location=" + response.getLocation() : "";
        return Optional.of(value);
    }
}
