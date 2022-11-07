package com.vizzionnaire.server.common.data.alarm;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.page.TimePageLink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by ashvayka on 11.05.17.
 */
@Data
@Builder
@AllArgsConstructor
public class AlarmQuery {

    private EntityId affectedEntityId;
    private TimePageLink pageLink;
    private AlarmSearchStatus searchStatus;
    private AlarmStatus status;
    private Boolean fetchOriginator;

}
