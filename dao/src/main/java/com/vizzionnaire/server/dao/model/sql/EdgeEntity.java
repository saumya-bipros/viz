package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;

import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.dao.util.mapping.JsonStringType;

import static com.vizzionnaire.server.dao.model.ModelConstants.EDGE_COLUMN_FAMILY_NAME;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = EDGE_COLUMN_FAMILY_NAME)
public class EdgeEntity extends AbstractEdgeEntity<Edge> {

    public EdgeEntity() {
        super();
    }

    public EdgeEntity(Edge edge) {
        super(edge);
    }

    @Override
    public Edge toData() {
        return super.toEdge();
    }
}
