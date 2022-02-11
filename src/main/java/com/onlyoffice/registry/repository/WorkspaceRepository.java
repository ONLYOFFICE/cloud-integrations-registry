package com.onlyoffice.registry.repository;

import com.onlyoffice.registry.model.Workspace;
import org.springframework.data.repository.CrudRepository;

public interface WorkspaceRepository extends CrudRepository<Workspace, String> {
}
