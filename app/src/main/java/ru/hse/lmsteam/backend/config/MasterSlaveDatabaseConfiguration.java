package ru.hse.lmsteam.backend.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.util.Assert;

@Configuration
@EnableR2dbcRepositories(basePackages = "ru.hse.lmsteam.backend.dao")
public class MasterSlaveDatabaseConfiguration {
  @Value("${spring.database.master.url}")
  private String masterDbURL;

  @Value("${spring.database.slave.url}")
  private String slaveDbURL;

  @Bean(name = "slaveConnectionFactory")
  @Primary
  public ConnectionFactory slaveConnectionFactory() {
    return getConnectionPool(slaveDbURL);
  }

  @Bean(name = "masterConnectionFactory")
  public ConnectionFactory masterConnectionFactory() {
    return getConnectionPool(masterDbURL);
  }

  @Bean(name = "slaveR2dbcEntityTemplate")
  @Primary
  public R2dbcEntityTemplate slaveR2dbcEntityTemplate(
      @Qualifier(value = "slaveConnectionFactory") ConnectionFactory connectionFactory) {
    return templateWithCustomConverters(connectionFactory, getCustomConverters());
  }

  @Bean(name = "masterR2dbcEntityTemplate")
  public R2dbcEntityTemplate masterR2dbcEntityTemplate(
      @Qualifier(value = "masterConnectionFactory") ConnectionFactory connectionFactory) {
    return templateWithCustomConverters(connectionFactory, getCustomConverters());
  }

  private R2dbcEntityTemplate templateWithCustomConverters(
      ConnectionFactory connectionFactory, Collection<?> converters) {

    Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
    var dialect = DialectResolver.getDialect(connectionFactory);
    var databaseClient =
        DatabaseClient.builder()
            .connectionFactory(connectionFactory)
            .bindMarkers(dialect.getBindMarkersFactory())
            .build();

    return new R2dbcEntityTemplate(
        databaseClient, new DefaultReactiveDataAccessStrategy(dialect, converters));
  }

  private List<ServiceLoader.Provider<Converter>> getCustomConverters() {
    return ServiceLoader.load(Converter.class).stream()
        .filter(converter -> converter.getClass().isAnnotationPresent(CustomConverter.class))
        .toList();
  }

  private ConnectionPool getConnectionPool(String url) {
    var connectionFactory = ConnectionFactories.get(url);
    var configuration = ConnectionPoolConfiguration.builder(connectionFactory).build();
    return new ConnectionPool(configuration);
  }
}
