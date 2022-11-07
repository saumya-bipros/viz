package com.vizzionnaire.server.dao.sql.rule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rule.RuleNode;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.RuleNodeEntity;
import com.vizzionnaire.server.dao.rule.RuleNodeDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JpaRuleNodeDao extends JpaAbstractSearchTextDao<RuleNodeEntity, RuleNode> implements RuleNodeDao {

    @Autowired
    private RuleNodeRepository ruleNodeRepository;

    @Override
    protected Class<RuleNodeEntity> getEntityClass() {
        return RuleNodeEntity.class;
    }

    @Override
    protected JpaRepository<RuleNodeEntity, UUID> getRepository() {
        return ruleNodeRepository;
    }

    @Override
    public List<RuleNode> findRuleNodesByTenantIdAndType(TenantId tenantId, String type, String search) {
        return DaoUtil.convertDataList(ruleNodeRepository.findRuleNodesByTenantIdAndType(tenantId.getId(), type, search));
    }

    @Override
    public PageData<RuleNode> findAllRuleNodesByType(String type, PageLink pageLink) {
        return DaoUtil.toPageData(ruleNodeRepository
                .findAllRuleNodesByType(
                        type,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public List<RuleNode> findByExternalIds(RuleChainId ruleChainId, List<RuleNodeId> externalIds) {
        return DaoUtil.convertDataList(ruleNodeRepository.findRuleNodesByRuleChainIdAndExternalIdIn(ruleChainId.getId(),
                externalIds.stream().map(RuleNodeId::getId).collect(Collectors.toList())));
    }

    @Override
    public void deleteByIdIn(List<RuleNodeId> ruleNodeIds) {
        ruleNodeRepository.deleteAllById(ruleNodeIds.stream().map(RuleNodeId::getId).collect(Collectors.toList()));
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.RULE_NODE;
    }

}
