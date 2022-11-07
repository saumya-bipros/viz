package com.vizzionnaire.server.service.telemetry.cmd.v2;

import lombok.Data;

import java.util.List;

import com.vizzionnaire.server.common.data.query.EntityKey;

@Data
public class LatestValueCmd {

    private List<EntityKey> keys;

}
