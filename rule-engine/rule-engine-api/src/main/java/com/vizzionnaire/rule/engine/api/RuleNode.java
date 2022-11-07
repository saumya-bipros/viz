package com.vizzionnaire.rule.engine.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vizzionnaire.server.common.data.plugin.ComponentScope;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.data.rule.RuleChainType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RuleNode {

    ComponentType type();

    String name();

    String nodeDescription();

    String nodeDetails();

    Class<? extends NodeConfiguration> configClazz();

    boolean inEnabled() default true;

    boolean outEnabled() default true;

    ComponentScope scope() default ComponentScope.TENANT;

    String[] relationTypes() default {"Success", "Failure"};

    String[] uiResources() default {};

    String configDirective() default "";

    String icon() default "";

    String iconUrl() default "";

    String docUrl() default "";

    boolean customRelations() default false;

    boolean ruleChainNode() default false;

    RuleChainType[] ruleChainTypes() default {RuleChainType.CORE, RuleChainType.EDGE};

}
