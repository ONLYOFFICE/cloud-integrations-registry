package com.onlyoffice.registry.jpa;

import com.onlyoffice.registry.RegistryApplicationTests;
import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.service.WorkspaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({"dev"})
public class WorkspaceTests extends RegistryApplicationTests {
    private final String workspaceTypeName = "dev";
    private final String workspaceID = "dev";
    private final String workspaceSecret = "placeholder";
    private final String workspaceHeader = "header";
    private final String workspaceUrl = "https://example.com";
    @Autowired
    private WorkspaceService workspaceService;

    @Test
    public void testSaveWorkspace() {
        this.workspaceService.saveWorkspace(
                workspaceTypeName,
                WorkspaceDTO
                        .builder()
                        .id(workspaceID)
                        .serverHeader(workspaceHeader)
                        .serverSecret(workspaceSecret)
                        .serverUrl(workspaceUrl)
                        .build()
                );
        assertDoesNotThrow(() -> this.workspaceService.getWorkspace(new WorkspaceID(workspaceID, workspaceTypeName)));
        this.workspaceService.deleteAllWorkspacesByType(workspaceTypeName);
    }

    @Test
    public void testRemoveOrphans() {
        this.workspaceService.saveWorkspace(
                workspaceTypeName,
                WorkspaceDTO
                        .builder()
                        .id(workspaceID)
                        .serverHeader(workspaceHeader)
                        .serverSecret(workspaceSecret)
                        .serverUrl(workspaceUrl)
                        .build()
        );
        this.workspaceService.deleteAllWorkspacesByType(workspaceTypeName);
        assertThrows(RuntimeException.class, () -> this.workspaceService.getWorkspace(new WorkspaceID(workspaceID, workspaceTypeName)).getId());
    }
}
