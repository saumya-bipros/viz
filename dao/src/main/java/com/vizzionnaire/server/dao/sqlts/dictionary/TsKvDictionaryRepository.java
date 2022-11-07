package com.vizzionnaire.server.dao.sqlts.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vizzionnaire.server.dao.model.sqlts.dictionary.TsKvDictionary;
import com.vizzionnaire.server.dao.model.sqlts.dictionary.TsKvDictionaryCompositeKey;
import com.vizzionnaire.server.dao.util.SqlTsOrTsLatestAnyDao;

import java.util.Optional;

@SqlTsOrTsLatestAnyDao
public interface TsKvDictionaryRepository extends JpaRepository<TsKvDictionary, TsKvDictionaryCompositeKey> {

    Optional<TsKvDictionary> findByKeyId(int keyId);

}