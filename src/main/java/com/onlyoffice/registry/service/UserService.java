package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.UserDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;

public interface UserService {
    UserDTO saveUser(UserDTO user, WorkspaceID workspaceID);
    UserDTO getUser(String userID, WorkspaceID workspaceID);
    void deleteUser(String userID, WorkspaceID workspaceID);
}
