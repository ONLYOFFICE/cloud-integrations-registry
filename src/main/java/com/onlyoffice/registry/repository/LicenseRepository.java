package com.onlyoffice.registry.repository;

import com.onlyoffice.registry.model.License;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LicenseRepository extends CrudRepository<License, String> {
    Optional<License> findLicenseByWorkspaceId(String workspaceId);
}
