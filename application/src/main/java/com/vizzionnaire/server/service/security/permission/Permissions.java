package com.vizzionnaire.server.service.security.permission;

import java.util.Optional;

public interface Permissions {

    Optional<PermissionChecker> getPermissionChecker(Resource resource);

}
