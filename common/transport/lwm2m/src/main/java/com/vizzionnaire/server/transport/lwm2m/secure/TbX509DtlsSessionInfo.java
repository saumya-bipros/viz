package com.vizzionnaire.server.transport.lwm2m.secure;

import lombok.Data;

import java.io.Serializable;

import com.vizzionnaire.server.common.transport.auth.ValidateDeviceCredentialsResponse;

@Data
public class TbX509DtlsSessionInfo implements Serializable {

    private final String x509CommonName;
    private final ValidateDeviceCredentialsResponse credentials;

}
