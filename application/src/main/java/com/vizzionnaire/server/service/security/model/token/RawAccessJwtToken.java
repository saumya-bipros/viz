package com.vizzionnaire.server.service.security.model.token;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.security.model.JwtToken;

public class RawAccessJwtToken implements JwtToken, Serializable {

    private static final long serialVersionUID = -797397445703066079L;

    private String token;

    public RawAccessJwtToken(String token) {
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }
}
