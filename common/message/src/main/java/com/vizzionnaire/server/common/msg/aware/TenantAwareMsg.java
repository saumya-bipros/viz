package com.vizzionnaire.server.common.msg.aware;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.TbActorMsg;

public interface TenantAwareMsg extends TbActorMsg {

	TenantId getTenantId();
	
}
