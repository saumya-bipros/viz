package com.vizzionnaire.server.transport.lwm2m.server.rpc;

import org.eclipse.leshan.core.link.DefaultLinkSerializer;
import org.eclipse.leshan.core.link.LinkSerializer;
import org.eclipse.leshan.core.request.DiscoverRequest;
import org.eclipse.leshan.core.response.DiscoverResponse;

import com.vizzionnaire.server.common.transport.TransportService;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;
import com.vizzionnaire.server.transport.lwm2m.server.downlink.DownlinkRequestCallback;

import java.util.Optional;

public class RpcDiscoverCallback extends RpcLwM2MDownlinkCallback<DiscoverRequest, DiscoverResponse> {

    private final LinkSerializer serializer = new DefaultLinkSerializer();

    public RpcDiscoverCallback(TransportService transportService, LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, DownlinkRequestCallback<DiscoverRequest, DiscoverResponse> callback) {
        super(transportService, client, requestMsg, callback);
    }

    protected Optional<String> serializeSuccessfulResponse(DiscoverResponse response) {
        return Optional.of(serializer.serialize(response.getObjectLinks()));
    }

}
