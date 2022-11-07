package com.vizzionnaire.server.dao.oauth2;

import com.vizzionnaire.server.common.data.oauth2.OAuth2Registration;
import com.vizzionnaire.server.common.data.oauth2.PlatformType;
import com.vizzionnaire.server.common.data.oauth2.SchemeType;
import com.vizzionnaire.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface OAuth2RegistrationDao extends Dao<OAuth2Registration> {

    List<OAuth2Registration> findEnabledByDomainSchemesDomainNameAndPkgNameAndPlatformType(List<SchemeType> domainSchemes, String domainName, String pkgName, PlatformType platformType);

    List<OAuth2Registration> findByOAuth2ParamsId(UUID oauth2ParamsId);

    String findAppSecret(UUID id, String pkgName);

}
