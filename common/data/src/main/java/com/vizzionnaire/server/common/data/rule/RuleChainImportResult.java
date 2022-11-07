package com.vizzionnaire.server.common.data.rule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Data;

@Data
public class RuleChainImportResult {

    @JsonIgnore
    private TenantId tenantId;
    private RuleChainId ruleChainId;
    private String ruleChainName;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean updated;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

}
