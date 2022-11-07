package com.vizzionnaire.server.dao.sql.oauth2;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vizzionnaire.server.dao.model.sql.OAuth2DomainEntity;

import java.util.List;
import java.util.UUID;

public interface OAuth2DomainRepository extends JpaRepository<OAuth2DomainEntity, UUID> {

    List<OAuth2DomainEntity> findByOauth2ParamsId(UUID oauth2ParamsId);

}

