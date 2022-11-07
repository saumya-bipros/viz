package com.vizzionnaire.server.transport.lwm2m.server.adaptors;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.vizzionnaire.server.common.transport.adaptor.AdaptorException;
import com.vizzionnaire.server.common.transport.adaptor.JsonConverter;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.util.TbLwM2mTransportComponent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Random;

@Slf4j
@Component("LwM2MJsonAdaptor")
@TbLwM2mTransportComponent
public class LwM2MJsonAdaptor implements LwM2MTransportAdaptor  {

    @Override
    public TransportProtos.PostTelemetryMsg convertToPostTelemetry(JsonElement jsonElement) throws AdaptorException {
        try {
            return JsonConverter.convertToTelemetryProto(jsonElement);
        } catch (IllegalStateException | JsonSyntaxException ex) {
            throw new AdaptorException(ex);
        }
    }

    @Override
    public TransportProtos.PostAttributeMsg convertToPostAttributes(JsonElement jsonElement) throws AdaptorException {
        try {
            return JsonConverter.convertToAttributesProto(jsonElement);
        } catch (IllegalStateException | JsonSyntaxException ex) {
            throw new AdaptorException(ex);
        }
    }

    @Override
    public TransportProtos.GetAttributeRequestMsg convertToGetAttributes(Collection<String> clientKeys, Collection<String> sharedKeys) throws AdaptorException {
        try {
            TransportProtos.GetAttributeRequestMsg.Builder result = TransportProtos.GetAttributeRequestMsg.newBuilder();
            Random random = new Random();
            result.setRequestId(random.nextInt());
            if (clientKeys != null) {
                result.addAllClientAttributeNames(clientKeys);
            }
            if (sharedKeys != null) {
                result.addAllSharedAttributeNames(sharedKeys);
            }
            return result.build();
        } catch (RuntimeException e) {
            throw new AdaptorException(e);
        }
    }
}
