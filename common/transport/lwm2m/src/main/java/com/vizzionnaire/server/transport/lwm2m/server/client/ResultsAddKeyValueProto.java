package com.vizzionnaire.server.transport.lwm2m.server.client;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.vizzionnaire.server.gen.transport.TransportProtos;

@Data
public class ResultsAddKeyValueProto {
    List<TransportProtos.KeyValueProto> resultAttributes;
    List<TransportProtos.KeyValueProto> resultTelemetries;

    public ResultsAddKeyValueProto() {
        this.resultAttributes = new ArrayList<>();
        this.resultTelemetries = new ArrayList<>();
    }

}
