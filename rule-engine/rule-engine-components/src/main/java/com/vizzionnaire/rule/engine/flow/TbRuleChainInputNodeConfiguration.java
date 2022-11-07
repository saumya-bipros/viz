package com.vizzionnaire.rule.engine.flow;

import lombok.Data;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;
import com.vizzionnaire.server.common.data.id.RuleChainId;

@Data
public class TbRuleChainInputNodeConfiguration implements NodeConfiguration<TbRuleChainInputNodeConfiguration> {

    private String ruleChainId;

    @Override
    public TbRuleChainInputNodeConfiguration defaultConfiguration() {
        return new TbRuleChainInputNodeConfiguration();
    }

}
