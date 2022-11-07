package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;

import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.dao.util.mapping.JsonStringType;

import static com.vizzionnaire.server.dao.model.ModelConstants.ASSET_COLUMN_FAMILY_NAME;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ASSET_COLUMN_FAMILY_NAME)
public final class AssetEntity extends AbstractAssetEntity<Asset> {

    public AssetEntity() {
        super();
    }

    public AssetEntity(Asset asset) {
        super(asset);
    }

    @Override
    public Asset toData() {
        return super.toAsset();
    }

}
