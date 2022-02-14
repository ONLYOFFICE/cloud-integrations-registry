package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.mapper.WorkspaceMapper;
import com.onlyoffice.registry.service.WorkspaceService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// TODO: Get stream of workspaces by type id
@RestController
@RequestMapping(path = "v1/workspace/{workspaceTypeName}")
@RateLimiter(name = "registryLimiter")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@Slf4j
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @Autowired
    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping(path = "/{workspaceID}")
    public ResponseEntity<WorkspaceDTO> getWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to get workspace of type: {} with id: {}", workspaceTypeName, workspaceID);
        WorkspaceDTO workspaceDTO = WorkspaceMapper
                .INSTANCE
                .toDTO(this.workspaceService.getWorkspace(workspaceID));

        workspaceDTO.add(
                linkTo(methodOn(WorkspaceController.class).getWorkspace(workspaceTypeName, workspaceID))
                        .withSelfRel(),
                linkTo(methodOn(WorkspaceController.class).saveWorkspace(workspaceTypeName, workspaceDTO))
                        .withRel("save"),
                linkTo(methodOn(WorkspaceController.class).deleteWorkspace(workspaceTypeName, workspaceID))
                        .withRel("delete")
        );

        return ResponseEntity.ok(workspaceDTO);
    }
    
    @PostMapping
    public ResponseEntity<WorkspaceDTO> saveWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @Valid @RequestBody WorkspaceDTO body
    ) {
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
    }

    @DeleteMapping(path = "/{workspaceID}")
    public ResponseEntity<GenericResponseDTO> deleteWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to delete workspace of type: {} with id: {}", workspaceTypeName, workspaceID);
        this.workspaceService.deleteWorkspace(workspaceID);
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .success(true)
                        .message("Workspace has been deleted")
                        .build()
        );
    }
}
