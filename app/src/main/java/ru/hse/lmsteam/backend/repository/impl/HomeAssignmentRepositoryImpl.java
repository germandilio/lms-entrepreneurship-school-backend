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
import ru.hse.lmsteam.backend.domain.HomeAssignment;
import ru.hse.lmsteam.backend.repository.HomeAssignmentRepository;
import ru.hse.lmsteam.backend.repository.query.translators.HomeAssignmentFilterOptionsQT;
import ru.hse.lmsteam.backend.service.model.assignments.HomeAssignmentFilterOptions;

@Repository
@RequiredArgsConstructor
public class HomeAssignmentRepositoryImpl implements HomeAssignmentRepository {
  private final MasterSlaveDbOperations db;
  private final HomeAssignmentFilterOptionsQT homeAssignmentFilterOptionsQT;

  @Override
  public Mono<HomeAssignment> findById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.slave.selectOne(query(where("id").is(id)), HomeAssignment.class);
  }

  @Override
  public Mono<HomeAssignment> update(HomeAssignment homeAssignment) {
    if (homeAssignment == null) {
      throw new IllegalArgumentException("HomeAssignment is null!");
    }
    return db.master.update(homeAssignment);
  }

  @Override
  public Mono<HomeAssignment> create(HomeAssignment homeAssignment) {
    if (homeAssignment == null) {
      throw new IllegalArgumentException("HomeAssignment is null!");
    }
    return db.master.insert(homeAssignment);
  }

  @Override
  public Mono<Long> delete(UUID homeAssignmentId) {
    if (homeAssignmentId == null) {
      throw new IllegalArgumentException("HomeAssignmentId is null!");
    }
    return db.master.delete(query(where("id").is(homeAssignmentId)), HomeAssignment.class);
  }

  @Override
  public Mono<Page<HomeAssignment>> findAll(
      HomeAssignmentFilterOptions filterOptions, Pageable pageable) {
    return db.slave
        .getDatabaseClient()
        .sql(homeAssignmentFilterOptionsQT.translateToSql(filterOptions, pageable))
        .mapProperties(HomeAssignment.class)
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
