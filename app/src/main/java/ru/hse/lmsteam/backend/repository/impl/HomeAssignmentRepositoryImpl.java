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
import ru.hse.lmsteam.backend.domain.Homework;
import ru.hse.lmsteam.backend.repository.HomeAssignmentRepository;
import ru.hse.lmsteam.backend.repository.query.translators.HomeAssignmentFilterOptionsQT;
import ru.hse.lmsteam.backend.service.model.tasks.HomeworkFilterOptions;

@Repository
@RequiredArgsConstructor
public class HomeAssignmentRepositoryImpl implements HomeAssignmentRepository {
  private final MasterSlaveDbOperations db;
  private final HomeAssignmentFilterOptionsQT homeAssignmentFilterOptionsQT;

  @Override
  public Mono<Homework> findById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.slave.selectOne(query(where("id").is(id)), Homework.class);
  }

  @Override
  public Mono<Homework> update(Homework homework) {
    if (homework == null) {
      throw new IllegalArgumentException("HomeAssignment is null!");
    }
    return db.master.update(homework);
  }

  @Override
  public Mono<Homework> create(Homework homework) {
    if (homework == null) {
      throw new IllegalArgumentException("HomeAssignment is null!");
    }
    return db.master.insert(homework);
  }

  @Override
  public Mono<Long> delete(UUID homeAssignmentId) {
    if (homeAssignmentId == null) {
      throw new IllegalArgumentException("HomeAssignmentId is null!");
    }
    return db.master.delete(query(where("id").is(homeAssignmentId)), Homework.class);
  }

  @Override
  public Mono<Page<Homework>> findAll(HomeworkFilterOptions filterOptions, Pageable pageable) {
    return db.slave
        .getDatabaseClient()
        .sql(homeAssignmentFilterOptionsQT.translateToSql(filterOptions, pageable))
        .mapProperties(Homework.class)
        .all()
        .collectList()
        .zipWith(
            db.slave
                .getDatabaseClient()
                .sql(homeAssignmentFilterOptionsQT.translateToCountSql(filterOptions))
                .mapValue(Long.class)
                .one())
        .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
  }
}
