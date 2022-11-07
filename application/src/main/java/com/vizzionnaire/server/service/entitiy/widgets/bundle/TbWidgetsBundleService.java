package com.vizzionnaire.server.service.entitiy.widgets.bundle;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;

public interface TbWidgetsBundleService {

    WidgetsBundle save(WidgetsBundle entity, User currentUser) throws Exception;

    void delete(WidgetsBundle entity) throws VizzionnaireException;
}
