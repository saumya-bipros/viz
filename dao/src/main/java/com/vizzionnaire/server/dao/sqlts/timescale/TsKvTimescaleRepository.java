package com.vizzionnaire.server.dao.sqlts.timescale;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.vizzionnaire.server.dao.model.sqlts.timescale.ts.TimescaleTsKvCompositeKey;
import com.vizzionnaire.server.dao.model.sqlts.timescale.ts.TimescaleTsKvEntity;
import com.vizzionnaire.server.dao.util.TimescaleDBTsOrTsLatestDao;

import java.util.List;
import java.util.UUID;

@TimescaleDBTsOrTsLatestDao
public interface TsKvTimescaleRepository extends JpaRepository<TimescaleTsKvEntity, TimescaleTsKvCompositeKey> {

    @Query("SELECT tskv FROM TimescaleTsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key = :entityKey " +
            "AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    List<TimescaleTsKvEntity> findAllWithLimit(
            @Param("entityId") UUID entityId,
            @Param("entityKey") int key,
            @Param("startTs") long startTs,
            @Param("endTs") long endTs, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM TimescaleTsKvEntity tskv WHERE tskv.entityId = :entityId " +
            "AND tskv.key = :entityKey " +
            "AND tskv.ts >= :startTs AND tskv.ts < :endTs")
    void delete(@Param("entityId") UUID entityId,
                @Param("entityKey") int key,
                @Param("startTs") long startTs,
                @Param("endTs") long endTs);

}
