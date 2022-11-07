package com.vizzionnaire.server.actors.tenant;

import com.vizzionnaire.server.common.msg.tools.TbRateLimits;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DebugTbRateLimits {

    private TbRateLimits tbRateLimits;
    private boolean ruleChainEventSaved;

}
