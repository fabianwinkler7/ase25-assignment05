package de.unibayreuth.se.taskboard.api.mapper;

import de.unibayreuth.se.taskboard.api.dtos.TaskDto;
import de.unibayreuth.se.taskboard.api.dtos.UserDto;
import de.unibayreuth.se.taskboard.business.domain.Task;
import de.unibayreuth.se.taskboard.business.exceptions.UserNotFoundException;
import de.unibayreuth.se.taskboard.business.ports.UserService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class TaskDtoMapper {

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserDtoMapper userDtoMapper;

    // ---------- Public API (wie bisher nutzbar) ----------
    public Task toBusiness(TaskDto source) {
        return toBusiness(source, new TimestampContext());
    }

    // ---------- MapStruct Mappings ----------

    // Domain -> DTO: assigneeId -> assignee(UserDto)
    @Mapping(target = "assignee", expression = "java(mapAssignee(source.getAssigneeId()))")
    public abstract TaskDto fromBusiness(Task source);

    // DTO -> Domain: assignee.id -> assigneeId
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "status", defaultValue = "TODO")
    @Mapping(target = "createdAt", expression = "java(mapTimestamp(source.getCreatedAt(), ctx))")
    @Mapping(target = "updatedAt", expression = "java(mapTimestamp(source.getUpdatedAt(), ctx))")
    protected abstract Task toBusiness(TaskDto source, @Context TimestampContext ctx);

    // ---------- Helper ----------

    protected UserDto mapAssignee(UUID userId) {
        if (userId == null) return null;

        try {
            return userDtoMapper.fromBusiness(userService.getById(userId));
        } catch (UserNotFoundException e) {
            // je nach gewünschtem Verhalten: null oder Exception weiterwerfen
            return null;
        }
    }

    protected LocalDateTime mapTimestamp(LocalDateTime timestamp, @Context TimestampContext ctx) {
        return (timestamp != null) ? timestamp : ctx.nowUtc;
    }

    public static class TimestampContext {
        final LocalDateTime nowUtc = LocalDateTime.now(ZoneId.of("UTC"));
    }
}
