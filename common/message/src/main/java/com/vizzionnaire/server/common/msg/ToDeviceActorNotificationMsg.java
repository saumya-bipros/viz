package com.vizzionnaire.server.common.msg;

import java.io.Serializable;

import com.vizzionnaire.server.common.msg.aware.DeviceAwareMsg;
import com.vizzionnaire.server.common.msg.aware.TenantAwareMsg;

/**
 * @author Andrew Shvayka
 */
public interface ToDeviceActorNotificationMsg extends TbActorMsg, TenantAwareMsg, DeviceAwareMsg, Serializable {

}
