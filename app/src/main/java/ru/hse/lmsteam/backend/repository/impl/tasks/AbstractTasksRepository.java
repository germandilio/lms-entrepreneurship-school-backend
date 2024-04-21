package ru.hse.lmsteam.backend.repository.impl.tasks;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.google.common.reflect.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.repository.TaskRepository;
import ru.hse.lmsteam.backend.repository.query.translators.PlainSQLQueryTranslator;

public abstract class AbstractTasksRepository<TaskType, Id, FOptionsType>
    implements TaskRepository<TaskType, Id, FOptionsType> {
  private final MasterSlaveDbOperations db;
  private final PlainSQLQueryTranslator<FOptionsType> filterOptionsQT;
  private final Class<TaskType> taskTypeClass;

  @SuppressWarnings("unchecked")
  public AbstractTasksRepository(
      MasterSlaveDbOperations db, PlainSQLQueryTranslator<FOptionsType> filterOptionsQT) {
    this.db = db;
    this.filterOptionsQT = filterOptionsQT;

    taskTypeClass = (Class<TaskType>) new TypeToken<TaskType>(getClass()) {}.getRawType();
  }

  @Override
  public Mono<TaskType> findById(Id id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.slave.selectOne(query(where("id").is(id)), taskTypeClass);
  }

  @Override
  public Mono<TaskType> update(TaskType task) {
    if (task == null) {
      throw new IllegalArgumentException("Task is null!");
    }
    return db.master.update(task);
  }

  @Override
  public Mono<TaskType> create(TaskType task) {
    if (task == null) {
      throw new IllegalArgumentException("Task is null!");
    }
    return db.master.insert(task);
  }

  @Override
  public Mono<Long> delete(Id id) {
    if (id == null) {
      throw new IllegalArgumentException("Task is null!");
    }
    return db.master.delete(query(where("id").is(id)), taskTypeClass);
  }

  @Override
  public Mono<Page<TaskType>> findAll(FOptionsType filterOptions, Pageable pageable) {
    return db.slave
        .getDatabaseClient()
        .sql(filterOptionsQT.translateToSql(filterOptions, pageable))
        .mapProperties(taskTypeClass)
        .all()
        .collectList()
        .zipWith(
            db.slave
                .getDatabaseClient()
                .sql(filterOptionsQT.translateToCountSql(filterOptions))
                .mapValue(Long.class)
                .one())
        .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
  }
}
