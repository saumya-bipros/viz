package com.vizzionnaire.server.common.data.device.credentials;

import lombok.Data;

@Data
public class BasicMqttCredentials {

    private String clientId;
    private String userName;
    private String password;

}
