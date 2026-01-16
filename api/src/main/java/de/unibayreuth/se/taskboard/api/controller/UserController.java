package de.unibayreuth.se.taskboard.api.controller;

import de.unibayreuth.se.taskboard.api.dtos.UserDto;
import de.unibayreuth.se.taskboard.api.mapper.UserDtoMapper;
import de.unibayreuth.se.taskboard.business.exceptions.MalformedRequestException;
import de.unibayreuth.se.taskboard.business.exceptions.UserNotFoundException;
import de.unibayreuth.se.taskboard.business.ports.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@OpenAPIDefinition(
        info = @Info(
                title = "TaskBoard",
                version = "0.0.1"
        )
)
@Tag(name = "Users")
@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;
        private final UserDtoMapper userDtoMapper;


        @GetMapping
        public ResponseEntity<List<UserDto>> getAll() {
                return ResponseEntity.ok(userService.getAll().stream()
                        .map(userDtoMapper::fromBusiness).toList()
                );
        }


        @GetMapping("/{id}")
        public ResponseEntity<UserDto> getById(@PathVariable UUID id) {
               try {
                     return ResponseEntity.ok(
                             userDtoMapper.fromBusiness(userService.getById(id))
                     );
               } catch (UserNotFoundException e) {
                       throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
               }
        }


        @PostMapping
        public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto userDto) {
                try {
                        return ResponseEntity.ok(
                                userDtoMapper.fromBusiness(
                                        userService.create(
                                                userDtoMapper.toBusiness(userDto)
                                        )
                                )
                        );
                } catch (MalformedRequestException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
                }
        }


}
