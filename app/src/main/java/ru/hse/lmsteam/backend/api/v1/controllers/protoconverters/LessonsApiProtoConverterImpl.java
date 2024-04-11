package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.Lesson;
import ru.hse.lmsteam.schema.api.lessons.CreateLesson;
import ru.hse.lmsteam.schema.api.lessons.GetLessons;

@Component
@RequiredArgsConstructor
public class LessonsApiProtoConverterImpl implements LessonsApiProtoConverter {
  private final LessonsApiProtoBuilder lessonsApiProtoBuilder;

  @Override
  public Lesson retrieveUpdateModel(CreateLesson.Request request) {
    var b = ru.hse.lmsteam.schema.api.lessons.Lesson.newBuilder();
    b.setTitle(request.getTitle());
    b.setLessonNumber(request.getLessonNumber());
    if (request.hasPublishDate()) {
      b.setPublishDate(request.getPublishDate());
    }
    if (request.hasDescription()) {
      b.setDescription(request.getDescription());
    }
    b.addAllVideoUrls(request.getVideoUrlsList());
    b.addAllPresentationUrls(request.getPresentationUrlsList());
    return lessonsApiProtoBuilder.toDomain(b.build());
  }

  @Override
  public ru.hse.lmsteam.schema.api.lessons.Lesson map(Lesson lesson)
      throws InvalidProtocolBufferException {
    return lessonsApiProtoBuilder.toProto(lesson);
  }

  @Override
  public Lesson map(ru.hse.lmsteam.schema.api.lessons.Lesson lesson) {
    return lessonsApiProtoBuilder.toDomain(lesson);
  }

  @Override
  public GetLessons.Response buildGetLessonsResponse(Page<Lesson> lessons) {
    return GetLessons.Response.newBuilder()
        .setPage(
            ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                .setTotalPages(lessons.getTotalPages())
                .setTotalElements(lessons.getTotalElements())
                .build())
        .addAllLessons(
            lessons.stream()
                .map(
                    lesson -> {
                      try {
                        return lessonsApiProtoBuilder.toProto(lesson);
                      } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                      }
                    })
                .toList())
        .build();
  }
}
