package ru.hse.lmsteam.backend.api.v1.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hse.lmsteam.backend.api.v1.schema.GroupsControllerDocSchema;

@RestController
@RequestMapping(
    value = "/api/v1/groups",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class GroupsController implements GroupsControllerDocSchema {}
