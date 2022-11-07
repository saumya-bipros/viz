package com.vizzionnaire.server.common.msg.aware;

import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.msg.TbActorMsg;

public interface RuleChainAwareMsg extends TbActorMsg {

	RuleChainId getRuleChainId();
	
}
