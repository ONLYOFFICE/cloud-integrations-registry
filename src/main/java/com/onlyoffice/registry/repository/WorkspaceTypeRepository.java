package com.onlyoffice.registry.repository;

import com.onlyoffice.registry.model.WorkspaceType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WorkspaceTypeRepository extends CrudRepository<WorkspaceType, Integer> {
    Optional<WorkspaceType> findWorkspaceTypeByName(String name);
    void deleteByName(String name);
}
