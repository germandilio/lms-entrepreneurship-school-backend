package ru.hse.lmsteam.backend.config.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;

/**
 * This is base class used to perform database operations on master and slave databases.
 *
 * <p>Usage example: @RequiredArgsConstructor public class EntityDbActionsImpls implements
 * EntityDbActions { private MasterSlaveDbActions dbOperations; }
 */
@RequiredArgsConstructor
public class MasterSlaveDbOperations {
  public final R2dbcEntityOperations master;
  public final R2dbcEntityOperations slave;
}
