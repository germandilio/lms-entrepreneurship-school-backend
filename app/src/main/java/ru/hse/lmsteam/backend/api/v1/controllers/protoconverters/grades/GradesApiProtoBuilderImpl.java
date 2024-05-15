package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.grades;

import java.util.*;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.grades.Grade;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.backend.domain.tasks.Task;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.backend.service.lesson.LessonManager;
import ru.hse.lmsteam.schema.api.grades.GetGrade;
import ru.hse.lmsteam.schema.api.grades.GetGrades;
import ru.hse.lmsteam.schema.api.grades.UpdateGrade;

@Component
@RequiredArgsConstructor
public class GradesApiProtoBuilderImpl implements GradesApiProtoBuilder {
  private final GradeProtoConverter gradeProtoConverter;
  private final LessonManager lessonManager;

  @Override
  public Mono<GetGrade.Response> buildGetGradeResponse(Grade grade) {
    return getLessonsCache(List.of(grade.task()))
        .map(
            lessonCache -> {
              var lessonOpt = tryRetrieveLesson(lessonCache).apply(grade.task());
              return GetGrade.Response.newBuilder()
                  .setGrade(gradeProtoConverter.map(grade, lessonOpt))
                  .build();
            });
  }

  @Override
  public Mono<GetGrades.Response> buildGetGradesResponse(Page<Grade> grades) {
    return getLessonsCache(grades.stream().map(Grade::task).toList())
        .map(
            lessonsCache -> {
              var getLessonFun = tryRetrieveLesson(lessonsCache);
              var protoGrades =
                  grades.stream()
                      .map(g -> gradeProtoConverter.map(g, getLessonFun.apply(g.task())))
                      .toList();
              var builder = GetGrades.Response.newBuilder();
              builder.addAllGrades(protoGrades);
              builder.setPage(
                  ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                      .setTotalElements(grades.getTotalElements())
                      .setTotalPages(grades.getTotalPages())
                      .build());
              return builder.build();
            });
  }

  @Override
  public Mono<UpdateGrade.Response> buildUpdateGradeResponse(Grade grade) {
    return getLessonsCache(List.of(grade.task()))
        .map(
            lessonCache -> {
              var lessonOpt = tryRetrieveLesson(lessonCache).apply(grade.task());
              return UpdateGrade.Response.newBuilder()
                  .setGrade(gradeProtoConverter.map(grade, lessonOpt))
                  .build();
            });
  }

  private Mono<Map<UUID, Lesson>> getLessonsCache(Collection<Task> tasks) {
    var lessonsId =
        tasks.stream()
            .flatMap(
                task -> {
                  if (task instanceof Homework hw) {
                    return Optional.ofNullable(hw.lessonId()).stream();
                  } else if (task instanceof Test test) {
                    return Optional.ofNullable(test.lessonId()).stream();
                  }
                  return Optional.<UUID>empty().stream();
                })
            .toList();

    return lessonManager.findByIds(lessonsId);
  }

  private Function<Task, Optional<Lesson>> tryRetrieveLesson(Map<UUID, Lesson> cache) {
    return task -> {
      if (task instanceof Homework hw) {
        return Optional.ofNullable(cache.get(hw.lessonId()));
      } else if (task instanceof Test test) {
        return Optional.ofNullable(cache.get(test.lessonId()));
      }
      return Optional.empty();
    };
  }
}
