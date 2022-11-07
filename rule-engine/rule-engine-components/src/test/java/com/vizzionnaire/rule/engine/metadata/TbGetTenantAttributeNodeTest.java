package com.vizzionnaire.rule.engine.metadata;

import com.google.common.util.concurrent.Futures;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.metadata.TbEntityGetAttrNode;
import com.vizzionnaire.rule.engine.metadata.TbGetTenantAttributeNode;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.UserId;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TbGetTenantAttributeNodeTest extends AbstractAttributeNodeTest {

    User user = new User();
    Asset asset = new Asset();
    Device device = new Device();

    @Before
    public void initDataForTests() throws TbNodeException {
        init(new TbGetTenantAttributeNode());
        user.setTenantId(tenantId);
        user.setId(new UserId(UUID.randomUUID()));

        asset.setTenantId(tenantId);
        asset.setId(new AssetId(UUID.randomUUID()));

        device.setTenantId(tenantId);
        device.setId(new DeviceId(UUID.randomUUID()));
    }

    @Override
    protected TbEntityGetAttrNode getEmptyNode() {
        return new TbGetTenantAttributeNode();
    }

    @Override
    EntityId getEntityId() {
        return tenantId;
    }

    @Test
    public void errorThrownIfCannotLoadAttributes() {
        mockFindUser(user);
        errorThrownIfCannotLoadAttributes(user);
    }

    @Test
    public void errorThrownIfCannotLoadAttributesAsync() {
        mockFindUser(user);
        errorThrownIfCannotLoadAttributesAsync(user);
    }

    @Test
    public void failedChainUsedIfCustomerCannotBeFound() {
        when(ctx.getUserService()).thenReturn(userService);
        when(userService.findUserByIdAsync(any(), eq(user.getId()))).thenReturn(Futures.immediateFuture(null));
        failedChainUsedIfCustomerCannotBeFound(user);
    }

    @Test
    public void customerAttributeAddedInMetadata() {
        entityAttributeAddedInMetadata(tenantId, "TENANT");
    }

    @Test
    public void usersCustomerAttributesFetched() {
        mockFindUser(user);
        usersCustomerAttributesFetched(user);
    }

    @Test
    public void assetsCustomerAttributesFetched() {
        mockFindAsset(asset);
        assetsCustomerAttributesFetched(asset);
    }

    @Test
    public void deviceCustomerAttributesFetched() {
        mockFindDevice(device);
        deviceCustomerAttributesFetched(device);
    }

    @Test
    public void deviceCustomerTelemetryFetched() throws TbNodeException {
        mockFindDevice(device);
        deviceCustomerTelemetryFetched(device);
    }
}
