package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.service.WorkspaceService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/workspace-type")
@RateLimiter(name = "workspaceTypeLimiter")
@AllArgsConstructor
@Slf4j
public class WorkspaceTypeController {
    private final WorkspaceService workspaceService;

    @DeleteMapping(path = "/{workspaceTypeName}")
    public ResponseEntity<GenericResponseDTO> deleteWorkspaceType(
            @PathVariable("workspaceTypeName") String workspaceTypeName
    ) {
        log.debug("call to delete workspace type with name: {}", workspaceTypeName);
        this.workspaceService.deleteAllWorkspacesByType(workspaceTypeName);
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .success(true)
                        .message("Workspace type has been deleted")
                        .build()
        );
    }
}