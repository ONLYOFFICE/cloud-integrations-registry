package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.mapper.WorkspaceMapper;
import com.onlyoffice.registry.model.Workspace;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.repository.WorkspaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class BasicWorkspaceService implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;

    @Transactional(
            isolation = Isolation.READ_UNCOMMITTED,
            rollbackFor = {TransactionException.class},
            timeout = 2
    )
    @Cacheable("workspaces")
    public WorkspaceDTO getWorkspace(WorkspaceID workspaceID) {
        log.debug("trying to get workspace of type: {} with id: {}", workspaceID.getWorkspaceType(), workspaceID.getWorkspaceId());
        return WorkspaceMapper.INSTANCE.toDTO(this.workspaceRepository
                .findById(workspaceID)
                .orElseThrow(() -> new RuntimeException("Could not get: Workspace with this id and type does not exist")));
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = {TransactionException.class, RuntimeException.class},
            timeout = 2
    )
    public WorkspaceDTO saveWorkspace(String workspaceTypeName, WorkspaceDTO workspaceDTO) {
        log.debug("trying to save workspace of type = {} with id = {}", workspaceTypeName, workspaceDTO.getId());
        Workspace workspace = WorkspaceMapper.INSTANCE.toEntity(workspaceDTO);
        workspace.getId().setWorkspaceType(workspaceTypeName);
        if (this.workspaceRepository.existsById(workspace.getId()))
            throw new RuntimeException("Could not save: Workspace with this id and workspace type already exists");
        this.workspaceRepository.save(workspace);
        return workspaceDTO;
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = {TransactionException.class, RuntimeException.class},
            timeout = 3
    )
    @CacheEvict("workspaces")
    public void deleteWorkspace(WorkspaceID workspaceID) {
        log.debug("trying to delete workspace of type: {} with id: {}", workspaceID.getWorkspaceType(), workspaceID.getWorkspaceId());
        try {
            this.workspaceRepository.deleteById(workspaceID);
        } catch (Exception e) {
            throw new RuntimeException("Could not delete: Workspace with this id and workspace type does not exist");
        }
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = {TransactionException.class, RuntimeException.class},
            timeout = 3
    )
    @CacheEvict("workspaces")
    public void deleteAllWorkspacesByType(String workspaceTypeName) {
        try {
            this.workspaceRepository.deleteAllByIdWorkspaceType(workspaceTypeName);
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not delete all workspaces of type "+workspaceTypeName);
        }
    }
}
