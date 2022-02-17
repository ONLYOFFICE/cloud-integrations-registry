package com.onlyoffice.registry.repository;

import com.onlyoffice.registry.model.User;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findUserByUserIdAndWorkspaceId(String userId, WorkspaceID workspaceID);
    void deleteUserByUserIdAndWorkspaceId(String userId, WorkspaceID workspaceID);
    boolean existsByUserIdAndWorkspaceId(String userId, WorkspaceID workspaceID);
}
