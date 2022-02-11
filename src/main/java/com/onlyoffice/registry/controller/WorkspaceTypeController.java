package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.WorkspaceTypeDTO;
import com.onlyoffice.registry.mapper.WorkspaceTypeMapper;
import com.onlyoffice.registry.service.WorkspaceTypeService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("v1/workspace-type")
@RateLimiter(name = "workspaceTypeLimiter")
@Slf4j
public class WorkspaceTypeController {
    private WorkspaceTypeService workspaceTypeService;
    @Autowired
    public WorkspaceTypeController(WorkspaceTypeService workspaceTypeService) {
        this.workspaceTypeService = workspaceTypeService;
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceTypeDTO>> getWorkspaceTypes() {
        log.debug("call to get workspace types");
        List<WorkspaceTypeDTO> workspaceTypes = WorkspaceTypeMapper.INSTANCE
                .toListDTO(this.workspaceTypeService.getWorkspaceTypes());
        return ResponseEntity.ok(workspaceTypes);
    }

    @GetMapping(path = "/{name}")
    public ResponseEntity<WorkspaceTypeDTO> getWorkspaceTypeByName(
            @PathVariable("name") String name
    ) {
        log.debug("call to get workspace type with name: {}", name);
        WorkspaceTypeDTO workspaceType = WorkspaceTypeMapper.INSTANCE
                .toDTO(this.workspaceTypeService.getWorkspaceType(name));
        return ResponseEntity.ok(workspaceType);
    }

    @PostMapping
    public ResponseEntity<GenericResponseDTO> createWorkspaceType(
            @Valid @RequestBody WorkspaceTypeDTO workspace
    ) {
        log.debug("call to create workspace type with name: {}", workspace.getName());
        this.workspaceTypeService.createWorkspaceType(workspace.getName());
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .message("Workspace type has been created")
                        .success(true)
                        .build()
        );
    }

    @DeleteMapping(path = "/{name}")
    public ResponseEntity<GenericResponseDTO> deleteWorkspaceType(
            @PathVariable("name") String name
    ) {
        log.debug("call to delete workspace type with name: {}", name);
        this.workspaceTypeService.deleteWorkspaceType(name);
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .success(true)
                        .message("Workspace type has been deleted")
                        .build()
        );
    }
}
