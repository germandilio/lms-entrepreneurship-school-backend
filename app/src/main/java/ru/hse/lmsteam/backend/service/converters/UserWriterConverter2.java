// package ru.hse.lmsteam.backend.domain.converters;
//
// import java.math.BigDecimal;
// import java.util.Optional;
// import java.util.UUID;
// import org.springframework.core.convert.converter.Converter;
// import org.springframework.data.convert.WritingConverter;
// import org.springframework.data.r2dbc.mapping.OutboundRow;
// import org.springframework.r2dbc.core.Parameter;
// import ru.hse.lmsteam.backend.config.persistence.CustomConverter;
// import ru.hse.lmsteam.backend.domain.user.User;
//
// @CustomConverter
// @WritingConverter
// public class UserWriterConverter2 implements Converter<User, OutboundRow> {
//  @Override
//  public OutboundRow convert(User source) {
//    var row = new OutboundRow();
//
//    row.put("id", Parameter.fromOrEmpty(source.id(), UUID.class));
//    row.put("name", Parameter.fromOrEmpty(source.name(), String.class));
//    row.put("surname", Parameter.fromOrEmpty(source.surname(), String.class));
//    row.put("patronymic", Parameter.fromOrEmpty(source.patronymic(), String.class));
//    row.put("messenger_contact", Parameter.fromOrEmpty(source.messengerContact(), String.class));
//    row.put(
//        "sex",
//        Parameter.fromOrEmpty(
//            Optional.ofNullable(source.sex()).map(Enum::name).orElse(null), String.class));
//    row.put("email", Parameter.fromOrEmpty(source.email(), String.class));
//    row.put("phone_number", Parameter.fromOrEmpty(source.phoneNumber(), String.class));
//    row.put("balance", Parameter.fromOrEmpty(source.balance(), BigDecimal.class));
//    row.put("is_deleted", Parameter.fromOrEmpty(source.isDeleted(), Boolean.class));
//    return row;
//  }
// }
