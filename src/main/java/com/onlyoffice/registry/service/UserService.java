package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.UserDTO;

public interface UserService {
    UserDTO saveUser(String workspaceID, UserDTO user);
    UserDTO getUser(String userID, String workspaceID);
    void deleteUser(String userID, String workspaceID);
}
