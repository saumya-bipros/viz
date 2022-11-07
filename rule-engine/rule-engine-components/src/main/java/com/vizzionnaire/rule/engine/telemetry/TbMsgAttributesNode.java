package com.vizzionnaire.rule.engine.telemetry;

import com.google.gson.JsonParser;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.kv.AttributeKvEntry;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.session.SessionMsgType;
import com.vizzionnaire.server.common.transport.adaptor.JsonConverter;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Set;

@Slf4j
@RuleNode(
        type = ComponentType.ACTION,
        name = "save attributes",
        configClazz = TbMsgAttributesNodeConfiguration.class,
        nodeDescription = "Saves attributes data",
        nodeDetails = "Saves entity attributes based on configurable scope parameter. Expects messages with 'POST_ATTRIBUTES_REQUEST' message type",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbActionNodeAttributesConfig",
        icon = "file_upload"
)
public class TbMsgAttributesNode implements TbNode {

    private TbMsgAttributesNodeConfiguration config;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbMsgAttributesNodeConfiguration.class);
        if (config.getNotifyDevice() == null) {
            config.setNotifyDevice(true);
        }
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        if (!msg.getType().equals(SessionMsgType.POST_ATTRIBUTES_REQUEST.name())) {
            ctx.tellFailure(msg, new IllegalArgumentException("Unsupported msg type: " + msg.getType()));
            return;
        }
        String src = msg.getData();
        Set<AttributeKvEntry> attributes = JsonConverter.convertToAttributes(new JsonParser().parse(src));
        String notifyDeviceStr = msg.getMetaData().getValue("notifyDevice");
        ctx.getTelemetryService().saveAndNotify(
                ctx.getTenantId(),
                msg.getOriginator(),
                config.getScope(),
                new ArrayList<>(attributes),
                config.getNotifyDevice() || StringUtils.isEmpty(notifyDeviceStr) || Boolean.parseBoolean(notifyDeviceStr),
                new TelemetryNodeCallback(ctx, msg)
        );
    }

    @Override
    public void destroy() {
    }

}
