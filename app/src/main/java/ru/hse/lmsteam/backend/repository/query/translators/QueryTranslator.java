package ru.hse.lmsteam.backend.repository.query.translators;

import org.springframework.data.relational.core.query.Query;

public interface QueryTranslator<T> {
  Query translate(T queryObject);
}
