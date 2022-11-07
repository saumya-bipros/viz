package com.vizzionnaire.server.common.data.oauth2;

import com.vizzionnaire.server.common.data.BaseData;
import com.vizzionnaire.server.common.data.id.OAuth2MobileId;
import com.vizzionnaire.server.common.data.id.OAuth2ParamsId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@NoArgsConstructor
public class OAuth2Mobile extends BaseData<OAuth2MobileId> {

    private OAuth2ParamsId oauth2ParamsId;
    private String pkgName;
    private String appSecret;

    public OAuth2Mobile(OAuth2Mobile mobile) {
        super(mobile);
        this.oauth2ParamsId = mobile.oauth2ParamsId;
        this.pkgName = mobile.pkgName;
        this.appSecret = mobile.appSecret;
    }
}
