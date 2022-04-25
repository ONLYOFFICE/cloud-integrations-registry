package com.onlyoffice.registry.service;

import com.onlyoffice.registry.InvalidRegistryOperationException;
import com.onlyoffice.registry.dto.UserDTO;
import com.onlyoffice.registry.mapper.UserMapper;
import com.onlyoffice.registry.model.User;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.repository.UserRepository;
import com.onlyoffice.registry.repository.WorkspaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = {
                    TransactionException.class,
                    InvalidRegistryOperationException.class,
                    RuntimeException.class
            },
            timeout = 2
    )
    public UserDTO saveUser(UserDTO user, WorkspaceID workspaceID) {
        User u = UserMapper.INSTANCE.toEntity(user);
        u.setWorkspace(this.workspaceRepository.getById(workspaceID));
        log.debug(
                "trying to save a new user with workspace id = {}, workspace type = {} and user id = {}",
                workspaceID.getWorkspaceId(), user.getId(), workspaceID.getWorkspaceType()
        );
        if (this.userRepository.existsByUserIdAndWorkspaceId(user.getId(), workspaceID))
            throw new InvalidRegistryOperationException("Could not save: User with this id, workspace id and workspace type already exists");
        this.userRepository.save(u);
        return user;
    }

    @Transactional(
            isolation = Isolation.READ_UNCOMMITTED,
            readOnly = true
    )
    public UserDTO getUser(String userID, WorkspaceID workspaceID) {
        log.debug("trying to get user with workspace id = {} and user id = {}", workspaceID.getWorkspaceId(), userID);
        User user = this.userRepository
                .findUserByUserIdAndWorkspaceId(userID, workspaceID)
                .orElseThrow(() -> new InvalidRegistryOperationException("Could not get: User does not exist"));
        return UserMapper.INSTANCE.toDto(user);
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = {
                    TransactionException.class,
                    InvalidRegistryOperationException.class,
                    RuntimeException.class
            },
            timeout = 3
    )
    public void deleteUser(String userID, WorkspaceID workspaceID) {
        log.debug("trying to delete user with workspace id = {} and user id = {}", workspaceID.getWorkspaceId(), userID);
        this.userRepository.deleteUserByUserIdAndWorkspaceId(userID, workspaceID);
    }
}
