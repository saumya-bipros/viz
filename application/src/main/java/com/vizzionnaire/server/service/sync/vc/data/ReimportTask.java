package com.vizzionnaire.server.service.sync.vc.data;

import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.common.data.sync.ie.EntityImportSettings;

import lombok.Data;

@Data
public class ReimportTask {

    private final EntityExportData data;
    private final EntityImportSettings settings;

}
