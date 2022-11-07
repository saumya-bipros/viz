package com.vizzionnaire.server.service.telemetry.cmd;

import lombok.Data;

import java.util.List;

import com.vizzionnaire.server.service.telemetry.cmd.v1.AttributesSubscriptionCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v1.GetHistoryCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v1.TimeseriesSubscriptionCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.AlarmDataCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.AlarmDataUnsubscribeCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityCountCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityCountUnsubscribeCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityDataCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityDataUnsubscribeCmd;

/**
 * @author Andrew Shvayka
 */
@Data
public class TelemetryPluginCmdsWrapper {

    private List<AttributesSubscriptionCmd> attrSubCmds;

    private List<TimeseriesSubscriptionCmd> tsSubCmds;

    private List<GetHistoryCmd> historyCmds;

    private List<EntityDataCmd> entityDataCmds;

    private List<EntityDataUnsubscribeCmd> entityDataUnsubscribeCmds;

    private List<AlarmDataCmd> alarmDataCmds;

    private List<AlarmDataUnsubscribeCmd> alarmDataUnsubscribeCmds;

    private List<EntityCountCmd> entityCountCmds;

    private List<EntityCountUnsubscribeCmd> entityCountUnsubscribeCmds;

}
