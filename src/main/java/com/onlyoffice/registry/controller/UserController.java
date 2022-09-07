package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.UserDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.service.UserService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "v1/workspaces/{workspaceTypeName}/{workspaceID}/users")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{userID}")
    @RateLimiter(name = "queryRateLimiter", fallbackMethod = "getUserRateFallback")
    @TimeLimiter(name = "queryTimeoutLimiter", fallbackMethod = "getUserTimeoutFallback")
    @Cacheable(value = "users", key = "{#workspaceTypeName, #workspaceID, #userID}")
    public CompletableFuture<ResponseEntity<UserDTO>> getUser(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @PathVariable("userID") String userID
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to get user with id: {}", userID);
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

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> getUserRateFallback(
            String workspaceTypeName,
            String workspaceID,
            String userID,
            RequestNotPermitted e
    ) {
        log.warn("get user {}({}):{} - {}", workspaceID, workspaceTypeName, userID, e.getMessage());
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> getUserTimeoutFallback(
            String workspaceTypeName,
            String workspaceID,
            String userID,
            TimeoutException e
    ) {
        log.warn("get user {}({}):{} - {}", workspaceID, workspaceTypeName, userID, e.getMessage());
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }

    @PostMapping
    @RateLimiter(name = "commandRateLimiter", fallbackMethod = "createUserFallback")
    public CompletableFuture<ResponseEntity<UserDTO>> createUser(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @Valid @RequestBody UserDTO user
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to create user with id: {}", user.getId());
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

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> createUserFallback(
            String workspaceTypeName,
            String workspaceID,
            UserDTO user,
            RequestNotPermitted e
    ) {
        log.warn("create user {}({}):{} - {}", workspaceID, workspaceTypeName, user.getId(), e.getMessage());
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }

    @DeleteMapping("/{userID}")
    @RateLimiter(name = "cleanupRateLimiter", fallbackMethod = "deleteUserFallback")
    @CacheEvict(value = "users", key = "{#workspaceTypeName, #workspaceID, #userID}")
    public CompletableFuture<ResponseEntity<GenericResponseDTO>> deleteUser(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @PathVariable("userID") String userID
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to delete user with id: {}", userID);
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

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> deleteUserFallback(
            String workspaceTypeName,
            String workspaceID,
            String userID,
            RequestNotPermitted e
    ) {
        log.warn("delete user {}({}):{} - {}", workspaceID, workspaceTypeName, userID, e.getMessage());
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }
}
