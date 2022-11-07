package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.CoapDeviceType;

import lombok.Data;

@Data
public class EfentoCoapDeviceTypeConfiguration implements CoapDeviceTypeConfiguration {

    @Override
    public CoapDeviceType getCoapDeviceType() {
        return CoapDeviceType.EFENTO;
    }
}
