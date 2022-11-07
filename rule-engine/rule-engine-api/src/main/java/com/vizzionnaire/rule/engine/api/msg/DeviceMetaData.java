package com.vizzionnaire.rule.engine.api.msg;

import com.vizzionnaire.server.common.data.id.DeviceId;

import lombok.Data;

/**
 * Contains basic device metadata;
 *
 * @author ashvayka
 */
@Data
public final class DeviceMetaData {

    final DeviceId deviceId;
    final String deviceName;
    final String deviceType;
    final DeviceAttributes deviceAttributes;

}
