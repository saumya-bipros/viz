package com.vizzionnaire.server.service.edge;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.sync.ie.importing.csv.BulkImportColumnType;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.edge.TbEdgeService;
import com.vizzionnaire.server.service.security.model.SecurityUser;
import com.vizzionnaire.server.service.sync.ie.importing.csv.AbstractBulkImportService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class EdgeBulkImportService extends AbstractBulkImportService<Edge> {
    private final EdgeService edgeService;
    private final TbEdgeService tbEdgeService;
    private final RuleChainService ruleChainService;

    @Override
    protected void setEntityFields(Edge entity, Map<BulkImportColumnType, String> fields) {
        ObjectNode additionalInfo = (ObjectNode) Optional.ofNullable(entity.getAdditionalInfo()).orElseGet(JacksonUtil::newObjectNode);
        fields.forEach((columnType, value) -> {
            switch (columnType) {
                case NAME:
                    entity.setName(value);
                    break;
                case TYPE:
                    entity.setType(value);
                    break;
                case LABEL:
                    entity.setLabel(value);
                    break;
                case DESCRIPTION:
                    additionalInfo.set("description", new TextNode(value));
                    break;
                case ROUTING_KEY:
                    entity.setRoutingKey(value);
                    break;
                case SECRET:
                    entity.setSecret(value);
                    break;
            }
        });
        entity.setAdditionalInfo(additionalInfo);
    }

    @SneakyThrows
    @Override
    protected Edge saveEntity(SecurityUser user, Edge entity, Map<BulkImportColumnType, String> fields) {
        RuleChain edgeTemplateRootRuleChain = ruleChainService.getEdgeTemplateRootRuleChain(user.getTenantId());
        return tbEdgeService.save(entity, edgeTemplateRootRuleChain, user);
    }

    @Override
    protected Edge findOrCreateEntity(TenantId tenantId, String name) {
        return Optional.ofNullable(edgeService.findEdgeByTenantIdAndName(tenantId, name))
                .orElseGet(Edge::new);
    }

    @Override
    protected void setOwners(Edge entity, SecurityUser user) {
        entity.setTenantId(user.getTenantId());
        entity.setCustomerId(user.getCustomerId());
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.EDGE;
    }

}
