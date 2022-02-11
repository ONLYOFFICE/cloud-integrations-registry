package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.UserDTO;
import com.onlyoffice.registry.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "v1/workspace/{workspaceTypeName}/{workspaceID}/user")
@RateLimiter(name = "registryLimiter")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@Slf4j
public class UserController {
    private UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userID}")
    public ResponseEntity<UserDTO> getUser(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @PathVariable("userID") String userID
    ) {
        log.debug("call to get user with id: {}", userID);
        UserDTO user = this.userService.getUser(userID, workspaceID);

        user.add(
                linkTo(methodOn(UserController.class).getUser(workspaceTypeName, workspaceID, userID))
                        .withSelfRel(),
                linkTo(methodOn(UserController.class).createUser(workspaceTypeName, workspaceID, user))
                        .withRel("create"),
                linkTo(methodOn(UserController.class).deleteUser(workspaceTypeName, workspaceID, userID))
                        .withRel("delete")
        );

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @Valid @RequestBody UserDTO user
    ) {
        log.debug("call to create user with id: {}", user.getId());
        UserDTO savedUser = this.userService.saveUser(workspaceID, user);

        savedUser.add(
                linkTo(methodOn(UserController.class).getUser(workspaceTypeName, workspaceID, user.getId()))
                        .withRel("get"),
                linkTo(methodOn(UserController.class).createUser(workspaceTypeName, workspaceID, user))
                        .withSelfRel(),
                linkTo(methodOn(UserController.class).deleteUser(workspaceTypeName, workspaceID, user.getId()))
                        .withRel("delete")
        );

        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{userID}")
    public ResponseEntity<GenericResponseDTO> deleteUser(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @PathVariable("userID") String userID
    ) {
        log.debug("call to delete user with id: {}", userID);
        this.userService.deleteUser(userID, workspaceID);
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .success(true)
                        .message("User has been deleted")
                        .build()
        );
    }
}
