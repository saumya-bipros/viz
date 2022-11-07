package com.vizzionnaire.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleChainType;
import com.vizzionnaire.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.rule.RuleChainDao;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.dao.service.DataValidator;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantService;

@Component
public class RuleChainDataValidator extends DataValidator<RuleChain> {

    @Autowired
    private RuleChainDao ruleChainDao;

    @Autowired
    @Lazy
    private RuleChainService ruleChainService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    @Override
    protected void validateCreate(TenantId tenantId, RuleChain data) {
        DefaultTenantProfileConfiguration profileConfiguration =
                (DefaultTenantProfileConfiguration) tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
        long maxRuleChains = profileConfiguration.getMaxRuleChains();
        validateNumberOfEntitiesPerTenant(tenantId, ruleChainDao, maxRuleChains, EntityType.RULE_CHAIN);
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, RuleChain ruleChain) {
        if (StringUtils.isEmpty(ruleChain.getName())) {
            throw new DataValidationException("Rule chain name should be specified!");
        }
        if (ruleChain.getType() == null) {
            ruleChain.setType(RuleChainType.CORE);
        }
        if (ruleChain.getTenantId() == null || ruleChain.getTenantId().isNullUid()) {
            throw new DataValidationException("Rule chain should be assigned to tenant!");
        }
        if (!tenantService.tenantExists(ruleChain.getTenantId())) {
            throw new DataValidationException("Rule chain is referencing to non-existent tenant!");
        }
        if (ruleChain.isRoot() && RuleChainType.CORE.equals(ruleChain.getType())) {
            RuleChain rootRuleChain = ruleChainService.getRootTenantRuleChain(ruleChain.getTenantId());
            if (rootRuleChain != null && !rootRuleChain.getId().equals(ruleChain.getId())) {
                throw new DataValidationException("Another root rule chain is present in scope of current tenant!");
            }
        }
        if (ruleChain.isRoot() && RuleChainType.EDGE.equals(ruleChain.getType())) {
            RuleChain edgeTemplateRootRuleChain = ruleChainService.getEdgeTemplateRootRuleChain(ruleChain.getTenantId());
            if (edgeTemplateRootRuleChain != null && !edgeTemplateRootRuleChain.getId().equals(ruleChain.getId())) {
                throw new DataValidationException("Another edge template root rule chain is present in scope of current tenant!");
            }
        }
    }
}
