package com.vizzionnaire.server.common.transport.limits;

import com.vizzionnaire.server.common.msg.tools.TbRateLimits;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleTransportRateLimit implements TransportRateLimit {

    private final TbRateLimits rateLimit;
    @Getter
    private final String configuration;

    public SimpleTransportRateLimit(String configuration) {
        this.configuration = configuration;
        this.rateLimit = new TbRateLimits(configuration);
    }

    @Override
    public boolean tryConsume() {
        return rateLimit.tryConsume();
    }

    @Override
    public boolean tryConsume(long number) {
        return number <= 0 || rateLimit.tryConsume(number);
    }
}
