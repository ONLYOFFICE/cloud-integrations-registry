package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.UserDTO;
import com.onlyoffice.registry.mapper.UserMapper;
import com.onlyoffice.registry.model.User;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.repository.UserRepository;
import com.onlyoffice.registry.repository.WorkspaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 3)
    public UserDTO saveUser(UserDTO user, WorkspaceID workspaceID) {
        User u = UserMapper.INSTANCE.toEntity(user);
        u.setWorkspace(this.workspaceRepository.getById(workspaceID));
        log.debug(
                "trying to save a new user with workspace id = {}, workspace type = {} and user id = {}",
                workspaceID.getWorkspaceId(), user.getId(), workspaceID.getWorkspaceType()
        );
        if (this.userRepository.existsByUserIdAndWorkspaceId(user.getId(), workspaceID))
            throw new RuntimeException("Could not save: User with this id, workspace id and workspace type already exists");
        this.userRepository.save(u);
        return user;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 3)
    @Cacheable("users")
    public UserDTO getUser(String userID, WorkspaceID workspaceID) {
        log.debug("trying to get user with workspace id = {} and user id = {}", workspaceID.getWorkspaceId(), userID);
        User user = this.userRepository
                .findUserByUserIdAndWorkspaceId(userID, workspaceID)
                .orElseThrow(() -> new RuntimeException("Could not get: User does not exist"));

        return UserMapper.INSTANCE.toDto(user);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 3)
    @CacheEvict("users")
    public void deleteUser(String userID, WorkspaceID workspaceID) {
        log.debug("trying to delete user with workspace id = {} and user id = {}", workspaceID.getWorkspaceId(), userID);
        try {
            this.userRepository
                    .deleteUserByUserIdAndWorkspaceId(userID, workspaceID);
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not delete: User does not exist");
        }
    }
}
