package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;

public interface WorkspaceService {
    WorkspaceDTO getWorkspace(WorkspaceID workspaceID);
    WorkspaceDTO saveWorkspace(String workspaceTypeName, WorkspaceDTO workspaceDTO);
    void deleteWorkspace(WorkspaceID workspaceID);
    void deleteAllWorkspacesByType(String workspaceTypeName);
}
