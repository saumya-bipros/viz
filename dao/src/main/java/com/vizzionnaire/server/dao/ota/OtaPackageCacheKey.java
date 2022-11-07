package com.vizzionnaire.server.dao.ota;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.id.OtaPackageId;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class OtaPackageCacheKey implements Serializable {

    private final OtaPackageId id;

    @Override
    public String toString() {
        return id.toString();
    }

}
