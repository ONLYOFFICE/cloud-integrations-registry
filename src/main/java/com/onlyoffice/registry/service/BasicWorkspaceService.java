package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.mapper.WorkspaceMapper;
import com.onlyoffice.registry.model.Workspace;
import com.onlyoffice.registry.repository.WorkspaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BasicWorkspaceService implements WorkspaceService {
    private WorkspaceRepository workspaceRepository;
    private BasicWorkspaceTypeService basicWorkspaceTypeService;

    @Autowired
    public BasicWorkspaceService(WorkspaceRepository workspaceRepository, BasicWorkspaceTypeService basicWorkspaceTypeService) {
        this.workspaceRepository = workspaceRepository;
        this.basicWorkspaceTypeService = basicWorkspaceTypeService;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 3)
    @Cacheable("workspaces")
    public Workspace getWorkspace(String workspaceID) {
        log.debug("trying to get workspace with id: {}", workspaceID);
        return this.workspaceRepository
                .findById(workspaceID)
                .orElseThrow(() -> new RuntimeException("Could not get: Workspace with this id/type does not exist"));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 3)
    public WorkspaceDTO saveWorkspace(String workspaceTypeName, WorkspaceDTO workspaceDTO) {
        log.debug("trying to save workspace of type = {} with id = {}", workspaceTypeName, workspaceDTO.getId());
        Workspace workspace = WorkspaceMapper.INSTANCE.toEntity(workspaceDTO);

        if (this.workspaceRepository.findById(workspace.getId()).isPresent())
            throw new RuntimeException("Could not save: Workspace already exists");

        workspace.setType(this.basicWorkspaceTypeService.getWorkspaceType(workspaceTypeName));

        try {
            this.workspaceRepository.save(workspace);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Could not save workspace with this parameters set");
        }

        return workspaceDTO;
    }

    @Override
    @Transactional(timeout = 3)
    @CacheEvict("workspaces")
    public void deleteWorkspace(String workspaceID) {
        try {
            log.debug("trying to delete workspace with id = {}", workspaceID);
            this.workspaceRepository.deleteById(workspaceID);
        } catch (Exception e) {
            throw new RuntimeException("Could not delete: Workspace with this id does not exist");
        }
    }
}
