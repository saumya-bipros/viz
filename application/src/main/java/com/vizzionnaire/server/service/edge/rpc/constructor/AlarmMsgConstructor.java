package com.vizzionnaire.server.service.edge.rpc.constructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EntityViewId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.dao.entityview.EntityViewService;
import com.vizzionnaire.server.gen.edge.v1.AlarmUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class AlarmMsgConstructor {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private EntityViewService entityViewService;

    public AlarmUpdateMsg constructAlarmUpdatedMsg(TenantId tenantId, UpdateMsgType msgType, Alarm alarm) {
        String entityName = null;
        switch (alarm.getOriginator().getEntityType()) {
            case DEVICE:
                entityName = deviceService.findDeviceById(tenantId, new DeviceId(alarm.getOriginator().getId())).getName();
                break;
            case ASSET:
                entityName = assetService.findAssetById(tenantId, new AssetId(alarm.getOriginator().getId())).getName();
                break;
            case ENTITY_VIEW:
                entityName = entityViewService.findEntityViewById(tenantId, new EntityViewId(alarm.getOriginator().getId())).getName();
                break;
        }
        AlarmUpdateMsg.Builder builder = AlarmUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(alarm.getId().getId().getMostSignificantBits())
                .setIdLSB(alarm.getId().getId().getLeastSignificantBits())
                .setName(alarm.getName())
                .setType(alarm.getType())
                .setOriginatorName(entityName)
                .setOriginatorType(alarm.getOriginator().getEntityType().name())
                .setSeverity(alarm.getSeverity().name())
                .setStatus(alarm.getStatus().name())
                .setStartTs(alarm.getStartTs())
                .setEndTs(alarm.getEndTs())
                .setAckTs(alarm.getAckTs())
                .setClearTs(alarm.getClearTs())
                .setDetails(JacksonUtil.toString(alarm.getDetails()))
                .setPropagate(alarm.isPropagate())
                .setPropagateToOwner(alarm.isPropagateToOwner())
                .setPropagateToTenant(alarm.isPropagateToTenant());
        return builder.build();
    }

}
