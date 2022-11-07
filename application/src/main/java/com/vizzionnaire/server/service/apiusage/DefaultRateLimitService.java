package com.vizzionnaire.server.service.apiusage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import com.vizzionnaire.server.common.msg.tools.TbRateLimits;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DefaultRateLimitService implements RateLimitService {

    private final TbTenantProfileCache tenantProfileCache;

    private final Map<String, Map<TenantId, TbRateLimits>> rateLimits = new ConcurrentHashMap<>();

    @Override
    public boolean checkEntityExportLimit(TenantId tenantId) {
        return checkLimit(tenantId, "entityExport", DefaultTenantProfileConfiguration::getTenantEntityExportRateLimit);
    }

    @Override
    public boolean checkEntityImportLimit(TenantId tenantId) {
        return checkLimit(tenantId, "entityImport", DefaultTenantProfileConfiguration::getTenantEntityImportRateLimit);
    }

    private boolean checkLimit(TenantId tenantId, String rateLimitsKey, Function<DefaultTenantProfileConfiguration, String> rateLimitConfigExtractor) {
        String rateLimitConfig = tenantProfileCache.get(tenantId).getProfileConfiguration()
                .map(rateLimitConfigExtractor).orElse(null);

        Map<TenantId, TbRateLimits> rateLimits = this.rateLimits.get(rateLimitsKey);
        if (StringUtils.isEmpty(rateLimitConfig)) {
            if (rateLimits != null) {
                rateLimits.remove(tenantId);
                if (rateLimits.isEmpty()) {
                    this.rateLimits.remove(rateLimitsKey);
                }
            }
            return true;
        }

        if (rateLimits == null) {
            rateLimits = new ConcurrentHashMap<>();
            this.rateLimits.put(rateLimitsKey, rateLimits);
        }
        TbRateLimits rateLimit = rateLimits.get(tenantId);
        if (rateLimit == null || !rateLimit.getConfiguration().equals(rateLimitConfig)) {
            rateLimit = new TbRateLimits(rateLimitConfig);
            rateLimits.put(tenantId, rateLimit);
        }

        return rateLimit.tryConsume();
    }

}
