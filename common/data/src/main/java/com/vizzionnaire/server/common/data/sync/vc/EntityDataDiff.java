package com.vizzionnaire.server.common.data.sync.vc;

import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityDataDiff {
    private EntityExportData<?> currentVersion;
    private EntityExportData<?> otherVersion;
}
