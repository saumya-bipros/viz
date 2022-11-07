package com.vizzionnaire.server.common.data.device.profile.lwm2m.bootstrap;

import com.vizzionnaire.server.common.data.device.credentials.lwm2m.LwM2MSecurityMode;

public class NoSecLwM2MBootstrapServerCredential extends AbstractLwM2MBootstrapServerCredential {
    @Override
    public LwM2MSecurityMode getSecurityMode() {
        return LwM2MSecurityMode.NO_SEC;
    }
}
