package com.vizzionnaire.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import static com.vizzionnaire.server.controller.ControllerConstants.EDGE_ID_PARAM_DESCRIPTION;
import static com.vizzionnaire.server.controller.ControllerConstants.EDGE_SORT_PROPERTY_ALLOWABLE_VALUES;
import static com.vizzionnaire.server.controller.ControllerConstants.PAGE_DATA_PARAMETERS;
import static com.vizzionnaire.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static com.vizzionnaire.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static com.vizzionnaire.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;
import static com.vizzionnaire.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static com.vizzionnaire.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.TimePageLink;
import com.vizzionnaire.server.dao.edge.EdgeEventService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.security.permission.Operation;

@Slf4j
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class EdgeEventController extends BaseController {

    @Autowired
    private EdgeEventService edgeEventService;

    public static final String EDGE_ID = "edgeId";

    @ApiOperation(value = "Get Edge Events (getEdgeEvents)",
            notes = "Returns a page of edge events for the requested edge. " +
                    PAGE_DATA_PARAMETERS, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/edge/{edgeId}/events", method = RequestMethod.GET)
    @ResponseBody
    public PageData<EdgeEvent> getEdgeEvents(
            @ApiParam(value = EDGE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable(EDGE_ID) String strEdgeId,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = "The case insensitive 'substring' filter based on the edge event type name.")
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = EDGE_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder,
            @ApiParam(value = "Timestamp. Edge events with creation time before it won't be queried")
            @RequestParam(required = false) Long startTime,
            @ApiParam(value = "Timestamp. Edge events with creation time after it won't be queried")
            @RequestParam(required = false) Long endTime) throws VizzionnaireException {
        checkParameter(EDGE_ID, strEdgeId);
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            EdgeId edgeId = new EdgeId(toUUID(strEdgeId));
            checkEdgeId(edgeId, Operation.READ);
            TimePageLink pageLink = createTimePageLink(pageSize, page, textSearch, sortProperty, sortOrder, startTime, endTime);
            return checkNotNull(edgeEventService.findEdgeEvents(tenantId, edgeId, pageLink, false));
        } catch (Exception e) {
            throw handleException(e);
        }
    }
}
