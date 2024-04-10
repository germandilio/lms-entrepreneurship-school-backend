package ru.hse.lmsteam.backend.repository.impl;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.Lesson;
import ru.hse.lmsteam.backend.repository.LessonRepository;
import ru.hse.lmsteam.backend.repository.query.translators.LessonsFilterOptionsQueryTranslator;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;

@Repository
@RequiredArgsConstructor
public class LessonRepositoryImpl implements LessonRepository {
  private final MasterSlaveDbOperations db;
  private final LessonsFilterOptionsQueryTranslator lessonsFilterOptionsQT;

  @Override
  public Mono<Lesson> findById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.slave.selectOne(query(where("id").is(id)), Lesson.class);
  }

  @Override
  public Mono<Lesson> update(Lesson lesson) {
    if (lesson == null) {
      throw new IllegalArgumentException("Lesson is null!");
    }
    if (lesson.id() == null) {
      throw new IllegalArgumentException("Lesson.id is null!");
    }
    return db.master.update(lesson);
  }

  @Override
  public Mono<Lesson> create(Lesson lesson) {
    if (lesson == null) {
      throw new IllegalArgumentException("Lesson is null!");
    }
    return db.master.insert(lesson);
  }

  @Override
  public Mono<Long> delete(UUID lessonId) {
    if (lessonId == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.master.delete(query(where("id").is(lessonId)), Lesson.class);
  }

  @Override
  public Mono<Page<Lesson>> findAll(LessonsFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null) {
      throw new IllegalArgumentException("FilterOptions is null!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable is null!");
    }
    return db.slave
        .getDatabaseClient()
        .sql(lessonsFilterOptionsQT.translateToSql(filterOptions, pageable))
        .mapProperties(Lesson.class)
        .all()
        .collectList()
        .zipWith(
            db.slave
                .getDatabaseClient()
                .sql(lessonsFilterOptionsQT.translateToCountSql(filterOptions))
                .mapValue(Long.class)
                .one())
        .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
  }
}
