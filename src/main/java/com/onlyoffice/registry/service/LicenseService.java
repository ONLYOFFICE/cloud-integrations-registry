package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.model.embeddable.LicenseCredentials;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;

public interface LicenseService {
    void saveLicense(WorkspaceID id, LicenseDTO licenseDTO);
    LicenseCredentials getLicense(WorkspaceID id);
}
