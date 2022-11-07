package com.vizzionnaire.server.common.data.oauth2;

import com.vizzionnaire.server.common.data.BaseData;
import com.vizzionnaire.server.common.data.id.OAuth2ParamsId;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@NoArgsConstructor
public class OAuth2Params extends BaseData<OAuth2ParamsId> {

    private boolean enabled;
    private TenantId tenantId;

    public OAuth2Params(OAuth2Params oauth2Params) {
        super(oauth2Params);
        this.enabled = oauth2Params.enabled;
        this.tenantId = oauth2Params.tenantId;
    }
}
