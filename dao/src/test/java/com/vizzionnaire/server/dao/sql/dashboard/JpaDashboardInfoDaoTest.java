package com.vizzionnaire.server.dao.sql.dashboard;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.vizzionnaire.server.common.data.DashboardInfo;
import com.vizzionnaire.server.common.data.id.DashboardId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.AbstractJpaDaoTest;
import com.vizzionnaire.server.dao.dashboard.DashboardInfoDao;
import com.vizzionnaire.server.dao.service.AbstractServiceTest;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
public class JpaDashboardInfoDaoTest extends AbstractJpaDaoTest {

    @Autowired
    private DashboardInfoDao dashboardInfoDao;

    @Test
    public void testFindDashboardsByTenantId() {
        UUID tenantId1 = Uuids.timeBased();
        UUID tenantId2 = Uuids.timeBased();

        for (int i = 0; i < 20; i++) {
            createDashboard(tenantId1, i);
            createDashboard(tenantId2, i * 2);
        }

        PageLink pageLink = new PageLink(15, 0, "DASHBOARD");
        PageData<DashboardInfo> dashboardInfos1 = dashboardInfoDao.findDashboardsByTenantId(tenantId1, pageLink);
        Assert.assertEquals(15, dashboardInfos1.getData().size());

        PageData<DashboardInfo> dashboardInfos2 = dashboardInfoDao.findDashboardsByTenantId(tenantId1, pageLink.nextPageLink());
        Assert.assertEquals(5, dashboardInfos2.getData().size());
    }

    private void createDashboard(UUID tenantId, int index) {
        DashboardInfo dashboardInfo = new DashboardInfo();
        dashboardInfo.setId(new DashboardId(Uuids.timeBased()));
        dashboardInfo.setTenantId(TenantId.fromUUID(tenantId));
        dashboardInfo.setTitle("DASHBOARD_" + index);
        dashboardInfoDao.save(AbstractServiceTest.SYSTEM_TENANT_ID, dashboardInfo);
    }
}
