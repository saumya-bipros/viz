package com.vizzionnaire.server.common.data.event;

import com.vizzionnaire.server.common.data.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel
public class RuleChainDebugEventFilter extends DebugEventFilter {

    @ApiModelProperty(position = 2, value = "String value representing the message")
    protected String message;

    @Override
    public EventType getEventType() {
        return EventType.DEBUG_RULE_CHAIN;
    }

    @Override
    public boolean isNotEmpty() {
        return super.isNotEmpty() || !StringUtils.isEmpty(message);
    }
}
