package com.vizzionnaire.server.service.sync.vc;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.RepositorySettings;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToVersionControlServiceMsg;

import java.util.UUID;

@RequiredArgsConstructor
@Data
public class VersionControlRequestCtx {
    private final String nodeId;
    private final UUID requestId;
    private final TenantId tenantId;
    private final RepositorySettings settings;

    public VersionControlRequestCtx(ToVersionControlServiceMsg msg, RepositorySettings settings) {
        this.nodeId = msg.getNodeId();
        this.requestId = new UUID(msg.getRequestIdMSB(), msg.getRequestIdLSB());
        this.tenantId = new TenantId(new UUID(msg.getTenantIdMSB(), msg.getTenantIdLSB()));
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "VersionControlRequestCtx{" +
                "nodeId='" + nodeId + '\'' +
                ", requestId=" + requestId +
                ", tenantId=" + tenantId +
                '}';
    }
}
