package com.vizzionnaire.server.transport.lwm2m.server.rpc;

import com.vizzionnaire.server.common.data.device.profile.lwm2m.ObjectAttributes;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RpcWriteAttributesRequest extends LwM2MRpcRequestHeader {

    private ObjectAttributes attributes;

}
