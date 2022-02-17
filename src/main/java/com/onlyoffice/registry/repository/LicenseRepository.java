package com.onlyoffice.registry.repository;

import com.onlyoffice.registry.model.License;
import org.springframework.data.repository.CrudRepository;

public interface LicenseRepository extends CrudRepository<License, String> {
}
