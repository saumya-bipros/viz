package com.vizzionnaire.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.ByteBuffer;

import com.vizzionnaire.server.common.data.id.OtaPackageId;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class OtaPackage extends OtaPackageInfo {

    private static final long serialVersionUID = 3091601761339422546L;

    @ApiModelProperty(position = 16, value = "OTA Package data.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private transient ByteBuffer data;

    public OtaPackage() {
        super();
    }

    public OtaPackage(OtaPackageId id) {
        super(id);
    }

    public OtaPackage(OtaPackage otaPackage) {
        super(otaPackage);
        this.data = otaPackage.getData();
    }
}
