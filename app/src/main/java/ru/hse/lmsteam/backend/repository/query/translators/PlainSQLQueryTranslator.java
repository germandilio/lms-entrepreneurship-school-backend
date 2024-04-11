package ru.hse.lmsteam.backend.repository.query.translators;

import org.springframework.data.domain.Pageable;

public interface PlainSQLQueryTranslator<T> {
  String translateToSql(T queryObject, Pageable pageable);

  String translateToCountSql(T queryObject);
}
