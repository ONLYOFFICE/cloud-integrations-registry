package com.onlyoffice.registry.service;

import com.onlyoffice.registry.model.WorkspaceType;

import java.util.List;

public interface WorkspaceTypeService {
    WorkspaceType getWorkspaceType(String name);
    List<WorkspaceType> getWorkspaceTypes();
    void createWorkspaceType(String name);
    void deleteWorkspaceType(String name);
}
