package com.vizzionnaire.server.service.sync.vc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.sync.vc.VersionCreationResult;
import com.vizzionnaire.server.common.data.sync.vc.VersionLoadResult;

@Data
@AllArgsConstructor
public class VersionControlTaskCacheEntry implements Serializable {

    private static final long serialVersionUID = -7875992200801588119L;

    private VersionCreationResult exportResult;
    private VersionLoadResult importResult;

    public static VersionControlTaskCacheEntry newForExport(VersionCreationResult result) {
        return new VersionControlTaskCacheEntry(result, null);
    }

    public static VersionControlTaskCacheEntry newForImport(VersionLoadResult result) {
        return new VersionControlTaskCacheEntry(null, result);
    }


}
