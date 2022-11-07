package com.vizzionnaire.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.server.common.data.asset.AssetInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetInfoEntity extends AbstractAssetEntity<AssetInfo> {

    public static final Map<String,String> assetInfoColumnMap = new HashMap<>();
    static {
        assetInfoColumnMap.put("customerTitle", "c.title");
    }

    private String customerTitle;
    private boolean customerIsPublic;

    public AssetInfoEntity() {
        super();
    }

    public AssetInfoEntity(AssetEntity assetEntity,
                           String customerTitle,
                           Object customerAdditionalInfo) {
        super(assetEntity);
        this.customerTitle = customerTitle;
        if (customerAdditionalInfo != null && ((JsonNode)customerAdditionalInfo).has("isPublic")) {
            this.customerIsPublic = ((JsonNode)customerAdditionalInfo).get("isPublic").asBoolean();
        } else {
            this.customerIsPublic = false;
        }
    }

    @Override
    public AssetInfo toData() {
        return new AssetInfo(super.toAsset(), customerTitle, customerIsPublic);
    }
}
