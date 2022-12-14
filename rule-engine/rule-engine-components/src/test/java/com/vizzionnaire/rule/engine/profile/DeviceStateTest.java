package com.vizzionnaire.rule.engine.profile;

import com.google.common.util.concurrent.Futures;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.rule.engine.api.RuleEngineAlarmService;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.profile.DeviceState;
import com.vizzionnaire.rule.engine.profile.ProfileState;
import com.vizzionnaire.rule.engine.profile.TbDeviceProfileNodeConfiguration;
import com.vizzionnaire.server.common.data.DataConstants;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.alarm.AlarmSeverity;
import com.vizzionnaire.server.common.data.device.profile.AlarmCondition;
import com.vizzionnaire.server.common.data.device.profile.AlarmConditionFilter;
import com.vizzionnaire.server.common.data.device.profile.AlarmConditionFilterKey;
import com.vizzionnaire.server.common.data.device.profile.AlarmConditionKeyType;
import com.vizzionnaire.server.common.data.device.profile.AlarmRule;
import com.vizzionnaire.server.common.data.device.profile.DeviceProfileAlarm;
import com.vizzionnaire.server.common.data.device.profile.DeviceProfileData;
import com.vizzionnaire.server.common.data.device.profile.SimpleAlarmConditionSpec;
import com.vizzionnaire.server.common.data.id.AlarmId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.query.BooleanFilterPredicate;
import com.vizzionnaire.server.common.data.query.EntityKeyValueType;
import com.vizzionnaire.server.common.data.query.FilterPredicateValue;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;
import com.vizzionnaire.server.common.msg.session.SessionMsgType;
import com.vizzionnaire.server.dao.attributes.AttributesService;
import com.vizzionnaire.server.dao.device.DeviceService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeviceStateTest {

    private TbContext ctx;

    @Before
    public void beforeEach() {
        ctx = mock(TbContext.class);

        when(ctx.getDeviceService()).thenReturn(mock(DeviceService.class));

        AttributesService attributesService = mock(AttributesService.class);
        when(attributesService.find(any(), any(), any(), anyCollection())).thenReturn(Futures.immediateFuture(Collections.emptyList()));
        when(ctx.getAttributesService()).thenReturn(attributesService);

        RuleEngineAlarmService alarmService = mock(RuleEngineAlarmService.class);
        when(alarmService.findLatestByOriginatorAndType(any(), any(), any())).thenReturn(Futures.immediateFuture(null));
        when(alarmService.createOrUpdateAlarm(any())).thenAnswer(invocationOnMock -> {
            Alarm alarm = invocationOnMock.getArgument(0);
            alarm.setId(new AlarmId(UUID.randomUUID()));
            return alarm;
        });
        when(ctx.getAlarmService()).thenReturn(alarmService);

        when(ctx.newMsg(any(), any(), any(), any(), any(), any())).thenAnswer(invocationOnMock -> {
            String data = invocationOnMock.getArgument(invocationOnMock.getArguments().length - 1);
            return TbMsg.newMsg(null, null, new TbMsgMetaData(), data);
        });

    }

    @Test
    public void whenAttributeIsDeleted_thenUnneededAlarmRulesAreNotReevaluated() throws Exception {

        DeviceProfileAlarm alarmConfig = createAlarmConfigWithBoolAttrCondition("enabled", false);
        DeviceId deviceId = new DeviceId(UUID.randomUUID());
        DeviceState deviceState = createDeviceState(deviceId, alarmConfig);

        TbMsg attributeUpdateMsg = TbMsg.newMsg(SessionMsgType.POST_ATTRIBUTES_REQUEST.name(),
                deviceId, new TbMsgMetaData(), "{ \"enabled\": false }");

        deviceState.process(ctx, attributeUpdateMsg);

        ArgumentCaptor<TbMsg> resultMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx).enqueueForTellNext(resultMsgCaptor.capture(), eq("Alarm Created"));
        Alarm alarm = JacksonUtil.fromString(resultMsgCaptor.getValue().getData(), Alarm.class);

        deviceState.process(ctx, TbMsg.newMsg(DataConstants.ALARM_CLEAR, deviceId, new TbMsgMetaData(), JacksonUtil.toString(alarm)));
        reset(ctx);

        String deletedAttributes = "{ \"attributes\": [ \"other\" ] }";
        deviceState.process(ctx, TbMsg.newMsg(DataConstants.ATTRIBUTES_DELETED, deviceId, new TbMsgMetaData(), deletedAttributes));
        verify(ctx, never()).enqueueForTellNext(any(), anyString());
    }

    @Test
    public void whenDeletingClearedAlarm_thenNoError() throws Exception {
        DeviceProfileAlarm alarmConfig = createAlarmConfigWithBoolAttrCondition("enabled", false);
        DeviceId deviceId = new DeviceId(UUID.randomUUID());
        DeviceState deviceState = createDeviceState(deviceId, alarmConfig);

        TbMsg attributeUpdateMsg = TbMsg.newMsg(SessionMsgType.POST_ATTRIBUTES_REQUEST.name(),
                deviceId, new TbMsgMetaData(), "{ \"enabled\": false }");

        deviceState.process(ctx, attributeUpdateMsg);
        ArgumentCaptor<TbMsg> resultMsgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx).enqueueForTellNext(resultMsgCaptor.capture(), eq("Alarm Created"));
        Alarm alarm = JacksonUtil.fromString(resultMsgCaptor.getValue().getData(), Alarm.class);

        deviceState.process(ctx, TbMsg.newMsg(DataConstants.ALARM_CLEAR, deviceId, new TbMsgMetaData(), JacksonUtil.toString(alarm)));

        TbMsg alarmDeleteNotification = TbMsg.newMsg(DataConstants.ALARM_DELETE, deviceId, new TbMsgMetaData(), JacksonUtil.toString(alarm));
        assertDoesNotThrow(() -> {
            deviceState.process(ctx, alarmDeleteNotification);
        });
    }


    private DeviceState createDeviceState(DeviceId deviceId, DeviceProfileAlarm... alarmConfigs) {
        DeviceProfile deviceProfile = new DeviceProfile();
        DeviceProfileData profileData = new DeviceProfileData();
        profileData.setAlarms(List.of(alarmConfigs));
        deviceProfile.setProfileData(profileData);

        ProfileState profileState = new ProfileState(deviceProfile);
        return new DeviceState(ctx, new TbDeviceProfileNodeConfiguration(),
                deviceId, profileState, null);
    }

    private DeviceProfileAlarm createAlarmConfigWithBoolAttrCondition(String key, boolean value) {

        AlarmConditionFilter condition = new AlarmConditionFilter();
        condition.setKey(new AlarmConditionFilterKey(AlarmConditionKeyType.ATTRIBUTE, key));
        condition.setValueType(EntityKeyValueType.BOOLEAN);
        BooleanFilterPredicate predicate = new BooleanFilterPredicate();
        predicate.setOperation(BooleanFilterPredicate.BooleanOperation.EQUAL);
        predicate.setValue(new FilterPredicateValue<>(value));
        condition.setPredicate(predicate);

        DeviceProfileAlarm alarmConfig = new DeviceProfileAlarm();
        alarmConfig.setId("MyAlarmID");
        alarmConfig.setAlarmType("MyAlarm");
        AlarmRule alarmRule = new AlarmRule();
        AlarmCondition alarmCondition = new AlarmCondition();
        alarmCondition.setSpec(new SimpleAlarmConditionSpec());
        alarmCondition.setCondition(List.of(condition));
        alarmRule.setCondition(alarmCondition);
        alarmConfig.setCreateRules(new TreeMap<>(Map.of(AlarmSeverity.CRITICAL, alarmRule)));

        return alarmConfig;
    }

}
