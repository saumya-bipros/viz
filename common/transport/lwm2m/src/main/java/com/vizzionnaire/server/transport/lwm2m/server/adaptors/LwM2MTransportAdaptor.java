package com.vizzionnaire.server.transport.lwm2m.server.adaptors;

import com.google.gson.JsonElement;
import com.vizzionnaire.server.common.transport.adaptor.AdaptorException;
import com.vizzionnaire.server.gen.transport.TransportProtos;

import java.util.Collection;

public interface LwM2MTransportAdaptor {

    TransportProtos.PostTelemetryMsg convertToPostTelemetry(JsonElement jsonElement) throws AdaptorException;

    TransportProtos.PostAttributeMsg convertToPostAttributes(JsonElement jsonElement) throws AdaptorException;

    TransportProtos.GetAttributeRequestMsg convertToGetAttributes(Collection<String> clientKeys, Collection<String> sharedKeys) throws AdaptorException;
}
