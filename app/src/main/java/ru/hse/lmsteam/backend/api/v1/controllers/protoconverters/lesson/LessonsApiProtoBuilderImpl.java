package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.lesson;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.schema.api.lessons.CreateOrUpdateLesson;
import ru.hse.lmsteam.schema.api.lessons.GetLesson;
import ru.hse.lmsteam.schema.api.lessons.GetLessons;

@Component
@RequiredArgsConstructor
public class LessonsApiProtoBuilderImpl implements LessonsApiProtoBuilder {
  private final LessonsProtoConverter lessonsProtoConverter;

  @Override
  public Lesson retrieveCreateModel(CreateOrUpdateLesson.Request request) {
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
    return lessonsProtoConverter.toDomain(b.build());
  }

  @Override
  public Mono<ru.hse.lmsteam.schema.api.lessons.Lesson> map(Lesson lesson) {
    return lessonsProtoConverter.toProto(lesson);
  }

  @Override
  public Lesson map(UUID id, ru.hse.lmsteam.schema.api.lessons.Lesson lesson) {
    return lessonsProtoConverter.toDomain(lesson.toBuilder().setId(id.toString()).build());
  }

  @Override
  public GetLessons.Response buildGetLessonsResponse(Page<Lesson> lessons) {
    return GetLessons.Response.newBuilder()
        .setPage(
            ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                .setTotalPages(lessons.getTotalPages())
                .setTotalElements(lessons.getTotalElements())
                .build())
        .addAllLessons(lessons.stream().map(lessonsProtoConverter::toSnippet).toList())
        .build();
  }

  @Override
  public Mono<GetLesson.Response> buildGetLessonResponse(Lesson lesson) {
    return map(lesson).map(l -> GetLesson.Response.newBuilder().setLesson(l).build());
  }
}
