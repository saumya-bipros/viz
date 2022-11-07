package com.vizzionnaire.server.common.msg.aware;

import com.vizzionnaire.server.common.data.id.CustomerId;

public interface CustomerAwareMsg {

	CustomerId getCustomerId();
	
}
