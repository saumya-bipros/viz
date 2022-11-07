package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;

import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.dao.model.ModelConstants;
import com.vizzionnaire.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.TENANT_COLUMN_FAMILY_NAME)
public final class TenantEntity extends AbstractTenantEntity<Tenant> {

    public TenantEntity() {
        super();
    }

    public TenantEntity(Tenant tenant) {
        super(tenant);
    }

    @Override
    public Tenant toData() {
        return super.toTenant();
    }
}
