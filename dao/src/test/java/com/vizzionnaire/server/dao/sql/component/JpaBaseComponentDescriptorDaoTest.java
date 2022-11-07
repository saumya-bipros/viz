package com.vizzionnaire.server.dao.sql.component;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.vizzionnaire.server.common.data.id.ComponentDescriptorId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.plugin.ComponentDescriptor;
import com.vizzionnaire.server.common.data.plugin.ComponentScope;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.dao.AbstractJpaDaoTest;
import com.vizzionnaire.server.dao.component.ComponentDescriptorDao;
import com.vizzionnaire.server.dao.service.AbstractServiceTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
public class JpaBaseComponentDescriptorDaoTest extends AbstractJpaDaoTest {

    final List<ComponentType> componentTypes = List.of(ComponentType.FILTER, ComponentType.ACTION);
    @Autowired
    private ComponentDescriptorDao componentDescriptorDao;

    @Before
    public void setUp() {
        for (int i = 0; i < 20; i++) {
            createComponentDescriptor(ComponentType.FILTER, ComponentScope.SYSTEM, i);
            createComponentDescriptor(ComponentType.ACTION, ComponentScope.TENANT, i + 20);
        }
    }

    @After
    public void tearDown() {
        for (ComponentType componentType : componentTypes) {
            List<ComponentDescriptor> byTypeAndPageLink = componentDescriptorDao.findByTypeAndPageLink(AbstractServiceTest.SYSTEM_TENANT_ID,
                    componentType, new PageLink(20)).getData();
            for (ComponentDescriptor descriptor : byTypeAndPageLink) {
                componentDescriptorDao.deleteById(AbstractServiceTest.SYSTEM_TENANT_ID, descriptor.getId());
            }
        }
    }

    @Test
    public void findByType() {
        PageLink pageLink = new PageLink(15, 0, "COMPONENT_");
        PageData<ComponentDescriptor> components1 = componentDescriptorDao.findByTypeAndPageLink(AbstractServiceTest.SYSTEM_TENANT_ID, ComponentType.FILTER, pageLink);
        assertEquals(15, components1.getData().size());

        pageLink = pageLink.nextPageLink();
        PageData<ComponentDescriptor> components2 = componentDescriptorDao.findByTypeAndPageLink(AbstractServiceTest.SYSTEM_TENANT_ID, ComponentType.FILTER, pageLink);
        assertEquals(5, components2.getData().size());
    }

    @Test
    public void findByTypeAndScope() {
        PageLink pageLink = new PageLink(15, 0, "COMPONENT_");
        PageData<ComponentDescriptor> components1 = componentDescriptorDao.findByScopeAndTypeAndPageLink(AbstractServiceTest.SYSTEM_TENANT_ID,
                ComponentScope.SYSTEM, ComponentType.FILTER, pageLink);
        assertEquals(15, components1.getData().size());

        pageLink = pageLink.nextPageLink();
        PageData<ComponentDescriptor> components2 = componentDescriptorDao.findByScopeAndTypeAndPageLink(AbstractServiceTest.SYSTEM_TENANT_ID,
                ComponentScope.SYSTEM, ComponentType.FILTER, pageLink);
        assertEquals(5, components2.getData().size());
    }

    private void createComponentDescriptor(ComponentType type, ComponentScope scope, int index) {
        ComponentDescriptor component = new ComponentDescriptor();
        component.setId(new ComponentDescriptorId(Uuids.timeBased()));
        component.setType(type);
        component.setScope(scope);
        component.setName("COMPONENT_" + index);
        componentDescriptorDao.save(AbstractServiceTest.SYSTEM_TENANT_ID, component);
    }

}
