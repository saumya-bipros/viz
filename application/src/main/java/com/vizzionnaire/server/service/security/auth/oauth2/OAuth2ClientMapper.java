package com.vizzionnaire.server.service.security.auth.oauth2;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.vizzionnaire.server.common.data.oauth2.OAuth2Registration;
import com.vizzionnaire.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletRequest;

public interface OAuth2ClientMapper {
    SecurityUser getOrCreateUserByClientPrincipal(HttpServletRequest request, OAuth2AuthenticationToken token, String providerAccessToken, OAuth2Registration registration);
}
