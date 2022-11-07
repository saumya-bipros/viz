package com.vizzionnaire.server.dao.sql.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.TbResourceInfo;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.TbResourceInfoEntity;
import com.vizzionnaire.server.dao.resource.TbResourceInfoDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class JpaTbResourceInfoDao extends JpaAbstractSearchTextDao<TbResourceInfoEntity, TbResourceInfo> implements TbResourceInfoDao {

    @Autowired
    private TbResourceInfoRepository resourceInfoRepository;

    @Override
    protected Class<TbResourceInfoEntity> getEntityClass() {
        return TbResourceInfoEntity.class;
    }

    @Override
    protected JpaRepository<TbResourceInfoEntity, UUID> getRepository() {
        return resourceInfoRepository;
    }

    @Override
    public PageData<TbResourceInfo> findAllTenantResourcesByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(resourceInfoRepository
                .findAllTenantResourcesByTenantId(
                        tenantId,
                        TenantId.NULL_UUID,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<TbResourceInfo> findTenantResourcesByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(resourceInfoRepository
                .findTenantResourcesByTenantId(
                        tenantId,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }
}
