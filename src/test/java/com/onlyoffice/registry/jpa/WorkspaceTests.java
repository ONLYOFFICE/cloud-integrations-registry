package com.onlyoffice.registry.jpa;

import com.onlyoffice.registry.RegistryApplicationTests;
import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.service.WorkspaceService;
import com.onlyoffice.registry.service.WorkspaceTypeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"dev"})
public class WorkspaceTests extends RegistryApplicationTests {
    private final String workspaceTypeName = "dev";
    private final String workspaceID = "dev";
    private final String workspaceSecret = "placeholder";
    private final String workspaceHeader = "header";
    private final String workspaceUrl = "https://example.com";
    @Autowired
    private WorkspaceTypeService workspaceTypeService;
    @Autowired
    private WorkspaceService workspaceService;

    @BeforeEach
    public void beforeEach() {
        this.workspaceTypeService.createWorkspaceType(workspaceTypeName);
    }

    @AfterEach
    public void afterEach() {
        this.workspaceTypeService.deleteWorkspaceType(workspaceTypeName);
    }

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
        assertDoesNotThrow(() -> this.workspaceService.getWorkspace(workspaceID));
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
        this.workspaceTypeService.deleteWorkspaceType(workspaceTypeName);
        assertThrows(RuntimeException.class, () -> this.workspaceService.getWorkspace(workspaceID));
        this.workspaceTypeService.createWorkspaceType(workspaceTypeName);
    }
}
