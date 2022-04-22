package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.mapper.LicenseMapper;
import com.onlyoffice.registry.model.License;
import com.onlyoffice.registry.model.embeddable.LicenseCredentials;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicLicenseService implements LicenseService {
    private final WorkspaceRepository workspaceRepository;

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = {TransactionException.class, RuntimeException.class},
            timeout = 2
    )
    public void saveLicense(WorkspaceID id, LicenseDTO licenseDTO) {
        log.debug("trying to update license. Workspace id = {}, workspace type = {}", id.getWorkspaceId(), id.getWorkspaceType());
        License license = this.workspaceRepository.getById(id).getLicense();
        LicenseCredentials credentials = LicenseMapper.INSTANCE.toEntity(licenseDTO);
        license.setCredentials(credentials);
    }

    @Transactional(
            isolation = Isolation.READ_UNCOMMITTED,
            rollbackFor = {TransactionException.class},
            timeout = 2
    )
    public LicenseCredentials getLicense(WorkspaceID id) {
        log.debug("trying to get license. Workspace id = {}, workspace type = {}", id.getWorkspaceId(), id.getWorkspaceType());
        return this.workspaceRepository.getById(id).getLicense().getCredentials();
    }
}
