package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.device.DeviceProfileService;

@AllArgsConstructor
@Slf4j
public class DeviceProfilesEdgeEventFetcher extends BasePageableEdgeEventFetcher<DeviceProfile> {

    private final DeviceProfileService deviceProfileService;

    @Override
    PageData<DeviceProfile> fetchPageData(TenantId tenantId, Edge edge, PageLink pageLink) {
        return deviceProfileService.findDeviceProfiles(tenantId, pageLink);
    }

    @Override
    EdgeEvent constructEdgeEvent(TenantId tenantId, Edge edge, DeviceProfile deviceProfile) {
        return EdgeUtils.constructEdgeEvent(tenantId, edge.getId(), EdgeEventType.DEVICE_PROFILE,
                EdgeEventActionType.ADDED, deviceProfile.getId(), null);
    }
}
