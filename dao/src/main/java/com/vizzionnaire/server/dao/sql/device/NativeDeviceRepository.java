package com.vizzionnaire.server.dao.sql.device;

import org.springframework.data.domain.Pageable;

import com.vizzionnaire.server.common.data.DeviceIdInfo;
import com.vizzionnaire.server.common.data.page.PageData;

public interface NativeDeviceRepository {

    PageData<DeviceIdInfo> findDeviceIdInfos(Pageable pageable);

}
