package ru.hse.lmsteam.backend.repository.query.translators;

import org.springframework.data.domain.Pageable;

public interface SimpleQueryTranslator<T> {
  String translateToSql(T queryObject, Pageable pageable);
}
