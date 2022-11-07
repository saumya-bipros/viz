package com.vizzionnaire.server.common.data.edge;

import com.vizzionnaire.server.common.data.id.EdgeId;

import lombok.Data;

@Data
public class EdgeInfo extends Edge {

    private String customerTitle;
    private boolean customerIsPublic;

    public EdgeInfo() {
        super();
    }

    public EdgeInfo(EdgeId edgeId) {
        super(edgeId);
    }

    public EdgeInfo(Edge edge, String customerTitle, boolean customerIsPublic) {
        super(edge);
        this.customerTitle = customerTitle;
        this.customerIsPublic = customerIsPublic;
    }
}