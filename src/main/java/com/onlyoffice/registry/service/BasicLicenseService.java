package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.mapper.LicenseMapper;
import com.onlyoffice.registry.model.License;
import com.onlyoffice.registry.model.embeddable.LicenseCredentials;
import com.onlyoffice.registry.repository.LicenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BasicLicenseService implements LicenseService {
    private LicenseRepository licenseRepository;

    @Autowired
    public BasicLicenseService(LicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 3)
    public void saveLicense(String workspaceID, LicenseDTO licenseDTO) {
        log.debug("trying to update license. Workspace id: {}", workspaceID);
        License license = this.licenseRepository
                .findLicenseByWorkspaceId(workspaceID)
                .orElseThrow(() -> new RuntimeException("Could not save: license with this workspace id does not exist"));
        LicenseCredentials credentials = LicenseMapper.INSTANCE.toEntity(licenseDTO);
        license.setCredentials(credentials);
    }

    @Override
    public LicenseCredentials getLicense(String workspaceID) {
        log.debug("trying to get license. Workspace id: {}", workspaceID);
        License license = this.licenseRepository
                .findLicenseByWorkspaceId(workspaceID)
                .orElseThrow(() -> new RuntimeException("Could not get: license with this workspace id does not exist"));
        return license.getCredentials();
    }
}
