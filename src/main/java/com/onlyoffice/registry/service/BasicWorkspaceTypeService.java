package com.onlyoffice.registry.service;

import com.onlyoffice.registry.model.WorkspaceType;
import com.onlyoffice.registry.repository.WorkspaceTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class BasicWorkspaceTypeService implements WorkspaceTypeService {
    private WorkspaceTypeRepository workspaceTypeRepository;

    @Autowired
    public BasicWorkspaceTypeService(WorkspaceTypeRepository workspaceTypeRepository) {
        this.workspaceTypeRepository = workspaceTypeRepository;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 3)
    @Cacheable("named-workspace-type")
    public WorkspaceType getWorkspaceType(String name) {
        log.debug("trying to get workspace type with name: {}", name);
        return this.workspaceTypeRepository
                .findWorkspaceTypeByName(name)
                .orElseThrow(() -> new RuntimeException("Workspace type with this name does not exist"));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 3)
    public List<WorkspaceType> getWorkspaceTypes() {
        log.debug("trying to get all workspace types");
        return StreamSupport
                .stream(this.workspaceTypeRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 3)
    public void createWorkspaceType(String name) {
        WorkspaceType type = new WorkspaceType();
        type.setName(name);
        try {
            log.debug("trying to create workspace type with name: {}", name);
            this.workspaceTypeRepository.save(type);
        } catch (Exception e) {
            throw new RuntimeException("Could not create a workspace type with this name. Perhaps it already exists");
        }
    }

    @Override
    @Transactional(timeout = 3)
    @CacheEvict("named-workspace-type")
    public void deleteWorkspaceType(String name) {
        try {
            log.debug("trying to delete workspace type with name: {}", name);
            this.workspaceTypeRepository.deleteByName(name);
        } catch (Exception e) {
            throw new RuntimeException("Could not delete workspace type with this name");
        }
    }
}
