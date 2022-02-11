package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.model.Workspace;

public interface WorkspaceService {
    Workspace getWorkspace(String workspaceID);
    WorkspaceDTO saveWorkspace(String workspaceTypeName, WorkspaceDTO workspaceDTO);
    void deleteWorkspace(String workspaceID);
}
