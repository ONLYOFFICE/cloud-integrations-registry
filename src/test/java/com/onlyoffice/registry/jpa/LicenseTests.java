package com.onlyoffice.registry.jpa;

import com.onlyoffice.registry.RegistryApplicationTests;
import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.service.BasicLicenseService;
import com.onlyoffice.registry.service.BasicWorkspaceService;
import com.onlyoffice.registry.service.BasicWorkspaceTypeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles({"dev"})
public class LicenseTests extends RegistryApplicationTests {
    private final String workspaceTypeName = "dev";
    private final String workspaceID = "dev";
    private final String licenseHeader = "header";
    private final String licenseSecret = "secret";
    private final String licenseUrl = "https://example.com";
    @Autowired
    private BasicWorkspaceTypeService workspaceTypeService;
    @Autowired
    private BasicWorkspaceService workspaceService;
    @Autowired
    private BasicLicenseService licenseService;

    @BeforeEach
    public void beforeEach() {
        this.workspaceTypeService.createWorkspaceType(workspaceTypeName);
        this.workspaceService.saveWorkspace(
                workspaceTypeName,
                WorkspaceDTO
                        .builder()
                        .id(workspaceID)
                        .serverUrl("https://placeholder.com")
                        .serverSecret("placeholder")
                        .serverHeader("placeholder")
                        .build()
                );
    }

    @AfterEach
    public void afterEach() {
        this.workspaceTypeService.deleteWorkspaceType(workspaceTypeName);
    }

    @Test
    public void testSaveLicense() {
        this.licenseService.saveLicense(
                workspaceID,
                LicenseDTO
                        .builder()
                        .serverUrl(licenseUrl)
                        .serverSecret(licenseSecret)
                        .serverHeader(licenseHeader)
                        .build()
        );
    }

    @Test
    public void testOrphanRemoval() {
        this.licenseService.saveLicense(
                workspaceID,
                LicenseDTO
                        .builder()
                        .serverUrl(licenseUrl)
                        .serverSecret(licenseSecret)
                        .serverHeader(licenseHeader)
                        .build()
        );
        this.workspaceService.deleteWorkspace(workspaceID);

        assertThrows(RuntimeException.class, () -> this.licenseService.getLicense(workspaceID));
    }
}
