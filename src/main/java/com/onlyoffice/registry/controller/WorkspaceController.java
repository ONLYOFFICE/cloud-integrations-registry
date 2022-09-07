package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.service.WorkspaceService;
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
@RequestMapping(path = "v1/workspaces/{workspaceTypeName}")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@RequiredArgsConstructor
@Slf4j
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @GetMapping(path = "/{workspaceID}")
    @RateLimiter(name = "queryRateLimiter", fallbackMethod = "getWorkspaceRateFallback")
    @TimeLimiter(name = "queryTimeoutLimiter", fallbackMethod = "getWorkspaceTimeoutFallback")
    @Cacheable(value = "workspaces", key = "{#workspaceTypeName, #workspaceID}")
    public CompletableFuture<ResponseEntity<WorkspaceDTO>> getWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to get workspace of type: {} with id: {}", workspaceTypeName, workspaceID);

            WorkspaceDTO workspaceDTO = this.workspaceService.getWorkspace(new WorkspaceID(workspaceID, workspaceTypeName));

            workspaceDTO.add(
                    linkTo(methodOn(WorkspaceController.class).getWorkspace(workspaceTypeName, workspaceID))
                            .withSelfRel(),
                    linkTo(methodOn(WorkspaceController.class).saveWorkspace(workspaceTypeName, workspaceDTO))
                            .withRel("save"),
                    linkTo(methodOn(WorkspaceController.class).deleteWorkspace(workspaceTypeName, workspaceID))
                            .withRel("delete")
            );

            return ResponseEntity.ok(workspaceDTO);
        });
    }

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> getWorkspaceRateFallback(
            String workspaceTypeName,
            String workspaceID,
            RequestNotPermitted e
    ) {
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> getWorkspaceTimeoutFallback(
            String workspaceTypeName,
            String workspaceID,
            TimeoutException e
    ) {
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }
    
    @PostMapping
    @RateLimiter(name = "commandRateLimiter", fallbackMethod = "saveWorkspaceFallback")
    public CompletableFuture<ResponseEntity<WorkspaceDTO>> saveWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @Valid @RequestBody WorkspaceDTO body
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to create workspace of type: {} with id: {}", workspaceTypeName, body.getId());

            WorkspaceDTO savedWorkspace = this.workspaceService.saveWorkspace(workspaceTypeName, body);

            savedWorkspace.add(
                    linkTo(methodOn(WorkspaceController.class).getWorkspace(workspaceTypeName, savedWorkspace.getId()))
                            .withRel("get"),
                    linkTo(methodOn(WorkspaceController.class).saveWorkspace(workspaceTypeName, savedWorkspace))
                            .withSelfRel(),
                    linkTo(methodOn(WorkspaceController.class).deleteWorkspace(workspaceTypeName, savedWorkspace.getId()))
                            .withRel("delete")
            );

            return ResponseEntity.ok(savedWorkspace);
        });
    }

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> saveWorkspaceFallback(
            String workspaceTypeName,
            WorkspaceDTO body,
            RequestNotPermitted e
    ) {
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }

    @DeleteMapping(path = "/{workspaceID}")
    @RateLimiter(name = "cleanupRateLimiter", fallbackMethod = "deleteWorkspaceFallback")
    @CacheEvict(value = "workspaces", key = "{#workspaceTypeName, #workspaceID}")
    public CompletableFuture<ResponseEntity<GenericResponseDTO>> deleteWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to delete workspace of type: {} with id: {}", workspaceTypeName, workspaceID);

            this.workspaceService.deleteWorkspace(new WorkspaceID(workspaceID, workspaceTypeName));
            return ResponseEntity.ok(
                    GenericResponseDTO
                            .builder()
                            .success(true)
                            .message("Workspace has been deleted")
                            .build()
            );
        });
    }

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> deleteWorkspaceFallback(
            String workspaceTypeName,
            String workspaceID,
            RequestNotPermitted e
    ) {
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }
}
