package com.vizzionnaire.mqtt.integration;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.runner.RunWith;

@RunWith(ClasspathSuite.class)
@ClasspathSuite.ClassnameFilters({
        "com.vizzionnaire.mqtt.integration.*Test",
})
public class IntegrationTestSuite {

}