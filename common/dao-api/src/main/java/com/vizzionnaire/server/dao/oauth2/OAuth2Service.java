package com.vizzionnaire.server.dao.oauth2;

import java.util.List;
import java.util.UUID;

import com.vizzionnaire.server.common.data.oauth2.OAuth2ClientInfo;
import com.vizzionnaire.server.common.data.oauth2.OAuth2Info;
import com.vizzionnaire.server.common.data.oauth2.OAuth2Registration;
import com.vizzionnaire.server.common.data.oauth2.PlatformType;

public interface OAuth2Service {
    List<OAuth2ClientInfo> getOAuth2Clients(String domainScheme, String domainName, String pkgName, PlatformType platformType);

    void saveOAuth2Info(OAuth2Info oauth2Info);

    OAuth2Info findOAuth2Info();

    OAuth2Registration findRegistration(UUID id);

    List<OAuth2Registration> findAllRegistrations();

    String findAppSecret(UUID registrationId, String pkgName);
}
