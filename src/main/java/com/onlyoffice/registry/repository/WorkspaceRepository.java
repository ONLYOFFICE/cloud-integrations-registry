package com.onlyoffice.registry.repository;

import com.onlyoffice.registry.model.Workspace;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, WorkspaceID> {
    void deleteAllByIdWorkspaceType(String workspaceType);
}
