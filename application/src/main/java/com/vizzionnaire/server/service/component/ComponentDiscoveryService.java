package com.vizzionnaire.server.service.component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.vizzionnaire.server.common.data.plugin.ComponentDescriptor;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.data.rule.RuleChainType;

/**
 * @author Andrew Shvayka
 */
public interface ComponentDiscoveryService {

    void discoverComponents();

    List<ComponentDescriptor> getComponents(ComponentType type, RuleChainType ruleChainType);

    List<ComponentDescriptor> getComponents(Set<ComponentType> types, RuleChainType ruleChainType);

    Optional<ComponentDescriptor> getComponent(String clazz);
}
