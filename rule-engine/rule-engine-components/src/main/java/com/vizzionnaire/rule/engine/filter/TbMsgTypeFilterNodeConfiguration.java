package com.vizzionnaire.rule.engine.filter;

import lombok.Data;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;
import com.vizzionnaire.server.common.msg.session.SessionMsgType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ashvayka on 19.01.18.
 */
@Data
public class TbMsgTypeFilterNodeConfiguration implements NodeConfiguration<TbMsgTypeFilterNodeConfiguration> {

    private List<String> messageTypes;

    @Override
    public TbMsgTypeFilterNodeConfiguration defaultConfiguration() {
        TbMsgTypeFilterNodeConfiguration configuration = new TbMsgTypeFilterNodeConfiguration();
        configuration.setMessageTypes(Arrays.asList(
                SessionMsgType.POST_ATTRIBUTES_REQUEST.name(),
                SessionMsgType.POST_TELEMETRY_REQUEST.name(),
                SessionMsgType.TO_SERVER_RPC_REQUEST.name()));
        return configuration;
    }
}
