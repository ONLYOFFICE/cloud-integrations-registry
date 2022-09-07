package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("v1/types")
@RequiredArgsConstructor
@Slf4j
public class WorkspaceTypeController {
    private final WorkspaceService workspaceService;
    private final CacheManager cacheManager;

    @DeleteMapping(path = "/{workspaceTypeName}")
    public CompletableFuture<ResponseEntity<GenericResponseDTO>> deleteWorkspaceType(
            @PathVariable("workspaceTypeName") String workspaceTypeName
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to delete workspace type with name: {}", workspaceTypeName);
            this.workspaceService.deleteAllWorkspacesByType(workspaceTypeName);
            cacheManager.getCache("workspaces").clear();
            return ResponseEntity.ok(
                    GenericResponseDTO
                            .builder()
                            .success(true)
                            .message("Workspace type has been deleted")
                            .build()
            );
        });
    }
}