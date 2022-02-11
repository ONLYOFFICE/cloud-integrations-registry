package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.model.embeddable.LicenseCredentials;

public interface LicenseService {
    void saveLicense(WorkspaceDTO workspace);
    LicenseCredentials getLicense(String workspaceID);
}
