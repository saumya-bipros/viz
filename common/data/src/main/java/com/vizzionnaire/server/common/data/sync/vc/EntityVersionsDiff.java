package com.vizzionnaire.server.common.data.sync.vc;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntityVersionsDiff {
    private EntityId externalId;
    private EntityExportData<?> entityDataAtVersion1;
    private EntityExportData<?> entityDataAtVersion2;
    private String rawDiff;
}
