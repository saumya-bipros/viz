package com.vizzionnaire.server.service.security.auth;

import com.vizzionnaire.server.service.security.model.SecurityUser;

public class MfaAuthenticationToken extends AbstractJwtAuthenticationToken {
    public MfaAuthenticationToken(SecurityUser securityUser) {
        super(securityUser);
    }
}
