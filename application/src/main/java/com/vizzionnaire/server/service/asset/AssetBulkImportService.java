package com.vizzionnaire.server.service.asset;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.ie.importing.csv.BulkImportColumnType;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.asset.TbAssetService;
import com.vizzionnaire.server.service.security.model.SecurityUser;
import com.vizzionnaire.server.service.sync.ie.importing.csv.AbstractBulkImportService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class AssetBulkImportService extends AbstractBulkImportService<Asset> {
    private final AssetService assetService;
    private final TbAssetService tbAssetService;

    @Override
    protected void setEntityFields(Asset entity, Map<BulkImportColumnType, String> fields) {
        ObjectNode additionalInfo = (ObjectNode) Optional.ofNullable(entity.getAdditionalInfo()).orElseGet(JacksonUtil::newObjectNode);
        fields.forEach((columnType, value) -> {
            switch (columnType) {
                case NAME:
                    entity.setName(value);
                    break;
                case TYPE:
                    entity.setType(value);
                    break;
                case LABEL:
                    entity.setLabel(value);
                    break;
                case DESCRIPTION:
                    additionalInfo.set("description", new TextNode(value));
                    break;
            }
        });
        entity.setAdditionalInfo(additionalInfo);
    }

    @Override
    @SneakyThrows
    protected Asset saveEntity(SecurityUser user, Asset entity, Map<BulkImportColumnType, String> fields) {
        return tbAssetService.save(entity, user);
    }

    @Override
    protected Asset findOrCreateEntity(TenantId tenantId, String name) {
        return Optional.ofNullable(assetService.findAssetByTenantIdAndName(tenantId, name))
                .orElseGet(Asset::new);
    }

    @Override
    protected void setOwners(Asset entity, SecurityUser user) {
        entity.setTenantId(user.getTenantId());
        entity.setCustomerId(user.getCustomerId());
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.ASSET;
    }

}
