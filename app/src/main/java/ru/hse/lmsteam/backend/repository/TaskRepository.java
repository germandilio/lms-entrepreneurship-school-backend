package ru.hse.lmsteam.backend.repository;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepository<T, K, FOptionsType> {
  Mono<T> findById(K id);

  Flux<T> findByIds(Collection<K> ids);

  Mono<T> update(T task);

  Mono<T> create(T task);

  Mono<Long> delete(K id);

  Mono<Page<T>> findAll(FOptionsType filterOptions, Pageable pageable);
}
