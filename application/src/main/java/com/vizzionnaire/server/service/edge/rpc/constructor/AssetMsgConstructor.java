package com.vizzionnaire.server.service.edge.rpc.constructor;

import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.gen.edge.v1.AssetUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class AssetMsgConstructor {

    public AssetUpdateMsg constructAssetUpdatedMsg(UpdateMsgType msgType, Asset asset, CustomerId customerId) {
        AssetUpdateMsg.Builder builder = AssetUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(asset.getId().getId().getMostSignificantBits())
                .setIdLSB(asset.getId().getId().getLeastSignificantBits())
                .setName(asset.getName())
                .setType(asset.getType());
        if (asset.getLabel() != null) {
            builder.setLabel(asset.getLabel());
        }
        if (customerId != null) {
            builder.setCustomerIdMSB(customerId.getId().getMostSignificantBits());
            builder.setCustomerIdLSB(customerId.getId().getLeastSignificantBits());
        }
        if (asset.getAdditionalInfo() != null) {
            builder.setAdditionalInfo(JacksonUtil.toString(asset.getAdditionalInfo()));
        }
        return builder.build();
    }

    public AssetUpdateMsg constructAssetDeleteMsg(AssetId assetId) {
        return AssetUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(assetId.getId().getMostSignificantBits())
                .setIdLSB(assetId.getId().getLeastSignificantBits()).build();
    }
}
