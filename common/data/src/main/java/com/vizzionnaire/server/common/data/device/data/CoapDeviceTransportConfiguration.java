package com.vizzionnaire.server.common.data.device.data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vizzionnaire.server.common.data.DeviceTransportType;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CoapDeviceTransportConfiguration extends PowerSavingConfiguration implements DeviceTransportConfiguration {

    @JsonIgnore
    private Map<String, Object> properties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> properties() {
        return this.properties;
    }

    @JsonAnySetter
    public void put(String name, Object value) {
        this.properties.put(name, value);
    }

    @Override
    public DeviceTransportType getType() {
        return DeviceTransportType.COAP;
    }

}
