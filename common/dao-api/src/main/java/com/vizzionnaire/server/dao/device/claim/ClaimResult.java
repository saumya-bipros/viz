package com.vizzionnaire.server.dao.device.claim;


import com.vizzionnaire.server.common.data.Device;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ClaimResult {

    private Device device;
    private ClaimResponse response;

}
