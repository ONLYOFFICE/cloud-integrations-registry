package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.model.embeddable.LicenseCredentials;

public interface LicenseService {
    void saveLicense(String workspaceID, LicenseDTO license);
    LicenseCredentials getLicense(String workspaceID);
}
