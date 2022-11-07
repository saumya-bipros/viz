package com.vizzionnaire.server.common.data.device.profile.lwm2m;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vizzionnaire.server.common.data.device.data.PowerMode;
import com.vizzionnaire.server.common.data.device.data.PowerSavingConfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtherConfiguration extends PowerSavingConfiguration {

    private Integer fwUpdateStrategy;
    private Integer swUpdateStrategy;
    private Integer clientOnlyObserveAfterConnect;
    private PowerMode powerMode;
    private Long psmActivityTimer;
    private Long edrxCycle;
    private Long pagingTransmissionWindow;
    private String fwUpdateResource;
    private String swUpdateResource;
}
