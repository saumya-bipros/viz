package com.vizzionnaire.server.queue.discovery;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.queue.discovery.QueueKey;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class QueueKeyTest {

    @Test
    void testToStringSystemTenant() {
        QueueKey queueKey = new QueueKey(ServiceType.TB_RULE_ENGINE, "Main", TenantId.SYS_TENANT_ID);
        log.info("The queue key is {}",queueKey);
        assertThat(queueKey.toString()).isEqualTo("QK(Main,TB_RULE_ENGINE,system)");
    }

    @Test
    void testToStringCustomTenant() {
        TenantId tenantId = TenantId.fromUUID(UUID.fromString("3ebd39eb-43d4-4911-a818-cdbf8d508f88"));
        QueueKey queueKey = new QueueKey(ServiceType.TB_RULE_ENGINE, "Main", tenantId);
        log.info("The queue key is {}",queueKey);
        assertThat(queueKey.toString()).isEqualTo("QK(Main,TB_RULE_ENGINE,3ebd39eb-43d4-4911-a818-cdbf8d508f88)");
    }
}
