package com.vizzionnaire.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.model.BaseSqlEntity;
import com.vizzionnaire.server.dao.model.ModelConstants;
import com.vizzionnaire.server.dao.model.SearchTextEntity;
import com.vizzionnaire.server.dao.util.mapping.JsonStringType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import static com.vizzionnaire.server.dao.model.ModelConstants.EDGE_CUSTOMER_ID_PROPERTY;
import static com.vizzionnaire.server.dao.model.ModelConstants.EDGE_LABEL_PROPERTY;
import static com.vizzionnaire.server.dao.model.ModelConstants.EDGE_NAME_PROPERTY;
import static com.vizzionnaire.server.dao.model.ModelConstants.EDGE_ROOT_RULE_CHAIN_ID_PROPERTY;
import static com.vizzionnaire.server.dao.model.ModelConstants.EDGE_ROUTING_KEY_PROPERTY;
import static com.vizzionnaire.server.dao.model.ModelConstants.EDGE_SECRET_PROPERTY;
import static com.vizzionnaire.server.dao.model.ModelConstants.EDGE_TENANT_ID_PROPERTY;
import static com.vizzionnaire.server.dao.model.ModelConstants.EDGE_TYPE_PROPERTY;
import static com.vizzionnaire.server.dao.model.ModelConstants.SEARCH_TEXT_PROPERTY;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractEdgeEntity<T extends Edge> extends BaseSqlEntity<T> implements SearchTextEntity<T> {

    @Column(name = EDGE_TENANT_ID_PROPERTY, columnDefinition = "uuid")
    private UUID tenantId;

    @Column(name = EDGE_CUSTOMER_ID_PROPERTY, columnDefinition = "uuid")
    private UUID customerId;

    @Column(name = EDGE_ROOT_RULE_CHAIN_ID_PROPERTY, columnDefinition = "uuid")
    private UUID rootRuleChainId;

    @Column(name = EDGE_TYPE_PROPERTY)
    private String type;

    @Column(name = EDGE_NAME_PROPERTY)
    private String name;

    @Column(name = EDGE_LABEL_PROPERTY)
    private String label;

    @Column(name = SEARCH_TEXT_PROPERTY)
    private String searchText;

    @Column(name = EDGE_ROUTING_KEY_PROPERTY)
    private String routingKey;

    @Column(name = EDGE_SECRET_PROPERTY)
    private String secret;

    @Type(type = "json")
    @Column(name = ModelConstants.EDGE_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;

    public AbstractEdgeEntity() {
        super();
    }

    public AbstractEdgeEntity(Edge edge) {
        if (edge.getId() != null) {
            this.setUuid(edge.getId().getId());
        }
        this.setCreatedTime(edge.getCreatedTime());
        if (edge.getTenantId() != null) {
            this.tenantId = edge.getTenantId().getId();
        }
        if (edge.getCustomerId() != null) {
            this.customerId = edge.getCustomerId().getId();
        }
        if (edge.getRootRuleChainId() != null) {
            this.rootRuleChainId = edge.getRootRuleChainId().getId();
        }
        this.type = edge.getType();
        this.name = edge.getName();
        this.label = edge.getLabel();
        this.routingKey = edge.getRoutingKey();
        this.secret = edge.getSecret();
        this.additionalInfo = edge.getAdditionalInfo();
    }

    public AbstractEdgeEntity(EdgeEntity edgeEntity) {
        this.setId(edgeEntity.getId());
        this.setCreatedTime(edgeEntity.getCreatedTime());
        this.tenantId = edgeEntity.getTenantId();
        this.customerId = edgeEntity.getCustomerId();
        this.rootRuleChainId = edgeEntity.getRootRuleChainId();
        this.type = edgeEntity.getType();
        this.name = edgeEntity.getName();
        this.label = edgeEntity.getLabel();
        this.searchText = edgeEntity.getSearchText();
        this.routingKey = edgeEntity.getRoutingKey();
        this.secret = edgeEntity.getSecret();
        this.additionalInfo = edgeEntity.getAdditionalInfo();
    }

    public String getSearchText() {
        return searchText;
    }

    @Override
    public String getSearchTextSource() {
        return name;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    protected Edge toEdge() {
        Edge edge = new Edge(new EdgeId(getUuid()));
        edge.setCreatedTime(createdTime);
        if (tenantId != null) {
            edge.setTenantId(TenantId.fromUUID(tenantId));
        }
        if (customerId != null) {
            edge.setCustomerId(new CustomerId(customerId));
        }
        if (rootRuleChainId != null) {
            edge.setRootRuleChainId(new RuleChainId(rootRuleChainId));
        }
        edge.setType(type);
        edge.setName(name);
        edge.setLabel(label);
        edge.setRoutingKey(routingKey);
        edge.setSecret(secret);
        edge.setAdditionalInfo(additionalInfo);
        return edge;
    }
}
