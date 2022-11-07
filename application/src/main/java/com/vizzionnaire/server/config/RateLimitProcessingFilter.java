package com.vizzionnaire.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.tools.TbRateLimits;
import com.vizzionnaire.server.common.msg.tools.TbRateLimitsException;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.exception.VizzionnaireErrorResponseHandler;
import com.vizzionnaire.server.service.security.model.SecurityUser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class RateLimitProcessingFilter extends GenericFilterBean {

    @Autowired
    private VizzionnaireErrorResponseHandler errorResponseHandler;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    private final ConcurrentMap<TenantId, TbRateLimits> perTenantLimits = new ConcurrentHashMap<>();
    private final ConcurrentMap<CustomerId, TbRateLimits> perCustomerLimits = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SecurityUser user = getCurrentUser();
        if (user != null && !user.isSystemAdmin()) {
            var profile = tenantProfileCache.get(user.getTenantId());
            if (profile == null) {
                log.debug("[{}] Failed to lookup tenant profile", user.getTenantId());
                errorResponseHandler.handle(new BadCredentialsException("Failed to lookup tenant profile"), (HttpServletResponse) response);
                return;
            }
            var profileConfiguration = profile.getDefaultProfileConfiguration();
            if (!checkRateLimits(user.getTenantId(), profileConfiguration.getTenantServerRestLimitsConfiguration(), perTenantLimits, response)) {
                return;
            }
            if (user.isCustomerUser()) {
                if (!checkRateLimits(user.getCustomerId(), profileConfiguration.getCustomerServerRestLimitsConfiguration(), perCustomerLimits, response)) {
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private <I extends EntityId> boolean checkRateLimits(I ownerId, String rateLimitConfig, Map<I, TbRateLimits> rateLimitsMap, ServletResponse response) {
        if (StringUtils.isNotEmpty(rateLimitConfig)) {
            TbRateLimits rateLimits = rateLimitsMap.get(ownerId);
            if (rateLimits == null || !rateLimits.getConfiguration().equals(rateLimitConfig)) {
                rateLimits = new TbRateLimits(rateLimitConfig);
                rateLimitsMap.put(ownerId, rateLimits);
            }

            if (!rateLimits.tryConsume()) {
                errorResponseHandler.handle(new TbRateLimitsException(ownerId.getEntityType()), (HttpServletResponse) response);
                return false;
            }
        } else {
            rateLimitsMap.remove(ownerId);
        }

        return true;
    }

    protected SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        } else {
            return null;
        }
    }

}
