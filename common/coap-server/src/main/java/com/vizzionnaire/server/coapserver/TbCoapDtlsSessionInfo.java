package com.vizzionnaire.server.coapserver;

import lombok.Data;

import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.transport.auth.ValidateDeviceCredentialsResponse;
import com.vizzionnaire.server.gen.transport.TransportProtos;

@Data
public class TbCoapDtlsSessionInfo {

    private ValidateDeviceCredentialsResponse msg;
    private DeviceProfile deviceProfile;
    private long lastActivityTime;


    public TbCoapDtlsSessionInfo(ValidateDeviceCredentialsResponse msg, DeviceProfile deviceProfile) {
        this.msg = msg;
        this.deviceProfile = deviceProfile;
        this.lastActivityTime = System.currentTimeMillis();
    }
}