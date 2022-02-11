package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.UserDTO;
import com.onlyoffice.registry.mapper.UserMapper;
import com.onlyoffice.registry.model.User;
import com.onlyoffice.registry.model.Workspace;
import com.onlyoffice.registry.model.embeddable.UserID;
import com.onlyoffice.registry.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BasicUserService implements UserService {
    private UserRepository userRepository;
    private BasicWorkspaceService basicWorkspaceService;

    @Autowired
    public BasicUserService(UserRepository userRepository, BasicWorkspaceService basicWorkspaceService) {
        this.userRepository = userRepository;
        this.basicWorkspaceService = basicWorkspaceService;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 3)
    public UserDTO saveUser(String workspaceID, UserDTO user) {
        Workspace workspace = this.basicWorkspaceService.getWorkspace(workspaceID);
        User u = UserMapper.INSTANCE.toEntity(user);
        u.getId().setWorkspaceID(workspaceID);
        if(this.userRepository.findById(u.getId()).isPresent())
            throw new RuntimeException("Could not save: User with this id already exists");
        u.setWorkspace(workspace);
        log.debug("trying to save a new user with workspace id = {} and user id = {}", workspaceID, user.getId());
        this.userRepository.save(u);
        return user;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 3)
    public UserDTO getUser(String userID, String workspaceID) {
        log.debug("trying to get user with workspace id = {} and user id = {}", workspaceID, userID);
        UserID id = UserID
                .builder()
                .workspaceID(workspaceID)
                .userID(userID)
                .build();
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not get: User does not exist"));

        return UserMapper.INSTANCE.toDto(user);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 3)
    public void deleteUser(String userID, String workspaceID) {
        log.debug("trying to delete user with workspace id = {} and user id = {}", workspaceID, userID);
        UserID id = UserID
                .builder()
                .userID(userID)
                .workspaceID(workspaceID)
                .build();
        User u = this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not delete: User does not exist"));
        this.userRepository.delete(u);
    }
}
