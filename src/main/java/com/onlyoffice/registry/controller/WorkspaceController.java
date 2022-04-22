package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.service.WorkspaceService;
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
@RequestMapping(path = "v1/workspace/{workspaceTypeName}")
@RateLimiter(name = "registryRateLimiter")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@RequiredArgsConstructor
@Slf4j
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @GetMapping(path = "/{workspaceID}")
    @TimeLimiter(name = "queryTimeoutLimiter", fallbackMethod = "getWorkspaceFallback")
    @Cacheable("workspaces")
    public CompletableFuture<ResponseEntity<WorkspaceDTO>> getWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to get workspace of type: {} with id: {}", workspaceTypeName, workspaceID);
        return CompletableFuture.supplyAsync(() -> {
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

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> getWorkspaceFallback(
            String workspaceTypeName,
            String workspaceID,
            TimeoutException rnp
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.badRequest()
                .body(GenericResponseDTO.builder()
                        .success(false)
                        .message(workspaceTypeName + " workspace fetching timeout: " + workspaceID)
                        .build()
                )
        );
    }
    
    @PostMapping
    public CompletableFuture<ResponseEntity<WorkspaceDTO>> saveWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @Valid @RequestBody WorkspaceDTO body
    ) {
        log.debug("call to create workspace of type: {} with id: {}", workspaceTypeName, body.getId());

        return CompletableFuture.supplyAsync(() -> {
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

    @DeleteMapping(path = "/{workspaceID}")
    public CompletableFuture<ResponseEntity<GenericResponseDTO>> deleteWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to delete workspace of type: {} with id: {}", workspaceTypeName, workspaceID);

        return CompletableFuture.supplyAsync(() -> {
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
}
