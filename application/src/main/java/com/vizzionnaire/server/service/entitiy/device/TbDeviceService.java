package com.vizzionnaire.server.service.entitiy.device;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;
import com.vizzionnaire.server.dao.device.claim.ClaimResult;
import com.vizzionnaire.server.dao.device.claim.ReclaimResult;

public interface TbDeviceService {

    Device save(Device device, Device oldDevice, String accessToken, User user) throws Exception;

    Device saveDeviceWithCredentials(Device device, DeviceCredentials deviceCredentials, User user) throws VizzionnaireException;

    ListenableFuture<Void> delete(Device device, User user);

    Device assignDeviceToCustomer(TenantId tenantId, DeviceId deviceId, Customer customer, User user) throws VizzionnaireException;

    Device unassignDeviceFromCustomer(Device device, Customer customer, User user) throws VizzionnaireException;

    Device assignDeviceToPublicCustomer(TenantId tenantId, DeviceId deviceId, User user) throws VizzionnaireException;

    DeviceCredentials getDeviceCredentialsByDeviceId(Device device, User user) throws VizzionnaireException;

    DeviceCredentials updateDeviceCredentials(Device device, DeviceCredentials deviceCredentials, User user) throws VizzionnaireException;

    ListenableFuture<ClaimResult> claimDevice(TenantId tenantId, Device device, CustomerId customerId, String secretKey, User user);

    ListenableFuture<ReclaimResult> reclaimDevice(TenantId tenantId, Device device, User user);

    Device assignDeviceToTenant(Device device, Tenant newTenant, User user);

    Device assignDeviceToEdge(TenantId tenantId, DeviceId deviceId, Edge edge, User user) throws VizzionnaireException;

    Device unassignDeviceFromEdge(Device device, Edge edge, User user) throws VizzionnaireException;
}
