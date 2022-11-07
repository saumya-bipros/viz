package com.vizzionnaire.server.dao.device;

import lombok.Data;

import java.util.List;

import com.vizzionnaire.server.dao.device.claim.ClaimData;

@Data
public class ClaimDataInfo {

    private final boolean fromCache;
    private final List<Object> key;
    private final ClaimData data;

}
