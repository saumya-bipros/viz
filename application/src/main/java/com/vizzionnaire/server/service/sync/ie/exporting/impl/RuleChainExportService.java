package com.vizzionnaire.server.service.sync.ie.exporting.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.common.util.RegexUtils;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleChainMetaData;
import com.vizzionnaire.server.common.data.sync.ie.RuleChainExportData;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesExportCtx;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class RuleChainExportService extends BaseEntityExportService<RuleChainId, RuleChain, RuleChainExportData> {

    private final RuleChainService ruleChainService;

    @Override
    protected void setRelatedEntities(EntitiesExportCtx<?> ctx, RuleChain ruleChain, RuleChainExportData exportData) {
        RuleChainMetaData metaData = ruleChainService.loadRuleChainMetaData(ctx.getTenantId(), ruleChain.getId());
        Optional.ofNullable(metaData.getNodes()).orElse(Collections.emptyList())
                .forEach(ruleNode -> {
                    ruleNode.setRuleChainId(null);
                    ctx.putExternalId(ruleNode.getId(), ruleNode.getExternalId());
                    ruleNode.setId(ctx.getExternalId(ruleNode.getId()));
                    ruleNode.setCreatedTime(0);
                    ruleNode.setExternalId(null);
                    replaceUuidsRecursively(ctx, ruleNode.getConfiguration(), Collections.emptySet());
                });
        Optional.ofNullable(metaData.getRuleChainConnections()).orElse(Collections.emptyList())
                .forEach(ruleChainConnectionInfo -> {
                    ruleChainConnectionInfo.setTargetRuleChainId(getExternalIdOrElseInternal(ctx, ruleChainConnectionInfo.getTargetRuleChainId()));
                });
        exportData.setMetaData(metaData);
        if (ruleChain.getFirstRuleNodeId() != null) {
            ruleChain.setFirstRuleNodeId(ctx.getExternalId(ruleChain.getFirstRuleNodeId()));
        }
    }

    @Override
    protected RuleChainExportData newExportData() {
        return new RuleChainExportData();
    }

    @Override
    public Set<EntityType> getSupportedEntityTypes() {
        return Set.of(EntityType.RULE_CHAIN);
    }

}
