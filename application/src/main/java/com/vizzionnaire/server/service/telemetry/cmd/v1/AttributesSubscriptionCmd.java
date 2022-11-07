package com.vizzionnaire.server.service.telemetry.cmd.v1;

import com.vizzionnaire.server.service.telemetry.TelemetryFeature;

import lombok.NoArgsConstructor;

/**
 * @author Andrew Shvayka
 */
@NoArgsConstructor
public class AttributesSubscriptionCmd extends SubscriptionCmd {

    @Override
    public TelemetryFeature getType() {
        return TelemetryFeature.ATTRIBUTES;
    }

}
