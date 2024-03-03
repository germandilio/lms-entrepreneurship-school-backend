package ru.hse.lmsteam.backend.model.converters;

import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import ru.hse.lmsteam.backend.config.persistence.CustomConverter;
import ru.hse.lmsteam.backend.model.Sex;

@CustomConverter
public class DemoConverter implements Converter<Row, Sex> {
  @Override
  public Sex convert(Row source) {
    return Sex.valueOf(source.get("sex", String.class));
  }
}
