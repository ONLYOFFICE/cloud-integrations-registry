package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.UserDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "v1/workspace/{workspaceTypeName}/{workspaceID}/user")
@RateLimiter(name = "registryRateLimiter")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{userID}")
    @TimeLimiter(name = "queryTimeoutLimiter", fallbackMethod = "getUserFallback")
    @Cacheable("users")
    public CompletableFuture<ResponseEntity<UserDTO>> getUser(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @PathVariable("userID") String userID
    ) {
        log.debug("call to get user with id: {}", userID);

        return CompletableFuture.supplyAsync(() -> {
            UserDTO user = this.userService.getUser(userID, new WorkspaceID(workspaceID, workspaceTypeName));

            user.add(
                    linkTo(methodOn(UserController.class).getUser(workspaceTypeName, workspaceID, userID))
                            .withSelfRel(),
                    linkTo(methodOn(UserController.class).createUser(workspaceTypeName, workspaceID, user))
                            .withRel("create"),
                    linkTo(methodOn(UserController.class).deleteUser(workspaceTypeName, workspaceID, userID))
                            .withRel("delete")
            );

            return ResponseEntity.ok(user);
        });
    }

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> getUserFallback(
            String workspaceTypeName,
            String workspaceID,
            String userID,
            TimeoutException rnp
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                .body(GenericResponseDTO.builder()
                        .success(false)
                        .message(workspaceTypeName + " user fetching timeout: " + userID)
                        .build()
                )
        );
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<UserDTO>> createUser(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @Valid @RequestBody UserDTO user
    ) {
        log.debug("call to create user with id: {}", user.getId());

        return CompletableFuture.supplyAsync(() -> {
            UserDTO savedUser = this.userService.saveUser(user, new WorkspaceID(workspaceID, workspaceTypeName));

            savedUser.add(
                    linkTo(methodOn(UserController.class).getUser(workspaceTypeName, workspaceID, user.getId()))
                            .withRel("get"),
                    linkTo(methodOn(UserController.class).createUser(workspaceTypeName, workspaceID, user))
                            .withSelfRel(),
                    linkTo(methodOn(UserController.class).deleteUser(workspaceTypeName, workspaceID, user.getId()))
                            .withRel("delete")
            );

            return ResponseEntity.ok(savedUser);
        });
    }

    @DeleteMapping("/{userID}")
    public CompletableFuture<ResponseEntity<GenericResponseDTO>> deleteUser(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @PathVariable("userID") String userID
    ) {
        log.debug("call to delete user with id: {}", userID);

        return CompletableFuture.supplyAsync(() -> {
            this.userService.deleteUser(userID, new WorkspaceID(workspaceID, workspaceTypeName));
            return ResponseEntity.ok(
                    GenericResponseDTO
                            .builder()
                            .success(true)
                            .message("User has been deleted")
                            .build()
            );
        });
    }
}
