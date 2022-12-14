package com.vizzionnaire.server.common.data.edge;

public enum EdgeEventActionType {
    ADDED,
    DELETED,
    UPDATED,
    POST_ATTRIBUTES,
    ATTRIBUTES_UPDATED,
    ATTRIBUTES_DELETED,
    TIMESERIES_UPDATED,
    CREDENTIALS_UPDATED,
    ASSIGNED_TO_CUSTOMER,
    UNASSIGNED_FROM_CUSTOMER,
    RELATION_ADD_OR_UPDATE,
    RELATION_DELETED,
    RPC_CALL,
    ALARM_ACK,
    ALARM_CLEAR,
    ASSIGNED_TO_EDGE,
    UNASSIGNED_FROM_EDGE,
    CREDENTIALS_REQUEST,
    ENTITY_MERGE_REQUEST
}