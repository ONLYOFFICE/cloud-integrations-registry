package com.onlyoffice.registry.mapper;

import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.model.*;
import com.onlyoffice.registry.model.embeddable.LicenseCredentials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkspaceTests {
    @Test
    public void shouldConvertToDTO() {
        WorkspaceType workspaceType = new WorkspaceType();
        workspaceType.setId(1);
        workspaceType.setName("workspace-test");
        workspaceType.setCreatedAt(LocalDateTime.now());

        License license = new License();
        license.setId(UUID.randomUUID().toString());
        LicenseCredentials credentials = LicenseCredentials
                .builder()
                .header("header")
                .secret("secret")
                .url("https://test.server.com")
                .build();
        license.setCredentials(credentials);

        Workspace workspace = new Workspace();
        workspace.setType(workspaceType);
        workspace.setLicense(license);
        workspace.setId(UUID.randomUUID().toString());
        workspace.setCreatedAt(LocalDateTime.now());

        WorkspaceDTO workspaceDTO = WorkspaceMapper.INSTANCE.toDTO(workspace);

        assertEquals(workspaceDTO.getId(), workspace.getId());
        assertEquals(workspaceDTO.getServerHeader(), workspace.getLicense().getCredentials().getHeader());
        assertEquals(workspaceDTO.getServerSecret(), workspace.getLicense().getCredentials().getSecret());
        assertEquals(workspaceDTO.getServerUrl(), workspace.getLicense().getCredentials().getUrl());
    }

    @Test
    public void shouldConvetToEntity() {
        WorkspaceDTO workspaceDTO = WorkspaceDTO
                .builder()
                .id(UUID.randomUUID().toString())
                .serverHeader("header")
                .serverSecret("secret")
                .serverUrl("https://server.test.com")
                .build();

        Workspace workspace = WorkspaceMapper.INSTANCE.toEntity(workspaceDTO);

        assertEquals(workspace.getId(), workspaceDTO.getId());
        assertEquals(workspace.getLicense().getCredentials().getUrl(), workspaceDTO.getServerUrl());
        assertEquals(workspace.getLicense().getCredentials().getSecret(), workspaceDTO.getServerSecret());
        assertEquals(workspace.getLicense().getCredentials().getUrl(), workspaceDTO.getServerUrl());
    }
}
