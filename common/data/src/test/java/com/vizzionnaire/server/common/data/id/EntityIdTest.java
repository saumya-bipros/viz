package com.vizzionnaire.server.common.data.id;

import org.junit.Assert;
import org.junit.Test;

import com.vizzionnaire.server.common.data.id.EntityId;

public class EntityIdTest {

    @Test
    public void givenConstantNullUuid_whenCompare_thenToStringEqualsPredefinedUuid() {
        Assert.assertEquals("13814000-1dd2-11b2-8080-808080808080", EntityId.NULL_UUID.toString());
    }

}