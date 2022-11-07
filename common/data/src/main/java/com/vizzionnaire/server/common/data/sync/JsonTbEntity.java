package com.vizzionnaire.server.common.data.sync;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.Dashboard;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.EntityView;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "entityType", include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
@JsonSubTypes({
        @Type(name = "DEVICE", value = Device.class),
        @Type(name = "RULE_CHAIN", value = RuleChain.class),
        @Type(name = "DEVICE_PROFILE", value = DeviceProfile.class),
        @Type(name = "ASSET", value = Asset.class),
        @Type(name = "DASHBOARD", value = Dashboard.class),
        @Type(name = "CUSTOMER", value = Customer.class),
        @Type(name = "ENTITY_VIEW", value = EntityView.class),
        @Type(name = "WIDGETS_BUNDLE", value = WidgetsBundle.class)
})
public @interface JsonTbEntity {
}
