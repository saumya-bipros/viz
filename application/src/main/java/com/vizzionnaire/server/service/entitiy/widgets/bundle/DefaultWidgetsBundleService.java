package com.vizzionnaire.server.service.entitiy.widgets.bundle;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;
import com.vizzionnaire.server.dao.widget.WidgetsBundleService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.AbstractTbEntityService;

@Service
@TbCoreComponent
@AllArgsConstructor
public class DefaultWidgetsBundleService extends AbstractTbEntityService implements TbWidgetsBundleService {

    private final WidgetsBundleService widgetsBundleService;

    @Override
    public WidgetsBundle save(WidgetsBundle widgetsBundle, User user) throws Exception {
        WidgetsBundle savedWidgetsBundle = checkNotNull(widgetsBundleService.saveWidgetsBundle(widgetsBundle));
        autoCommit(user, savedWidgetsBundle.getId());
        notificationEntityService.notifySendMsgToEdgeService(widgetsBundle.getTenantId(), savedWidgetsBundle.getId(),
                widgetsBundle.getId() == null ? EdgeEventActionType.ADDED : EdgeEventActionType.UPDATED);
        return savedWidgetsBundle;
    }

    @Override
    public void delete(WidgetsBundle widgetsBundle) throws ThingsboardException {
        widgetsBundleService.deleteWidgetsBundle(widgetsBundle.getTenantId(), widgetsBundle.getId());
        notificationEntityService.notifySendMsgToEdgeService(widgetsBundle.getTenantId(), widgetsBundle.getId(),
                EdgeEventActionType.DELETED);
    }
}
