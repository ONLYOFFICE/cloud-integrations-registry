package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.DemoInfoDTO;
import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.service.DemoService;
import com.onlyoffice.registry.service.LicenseService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "v1/workspace/{workspaceTypeName}/{workspaceID}")
@RateLimiter(name = "registryLimiter")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@Slf4j
public class LicenseController {
    private final LicenseService licenseService;
    private final DemoService demoService;

    @Autowired
    public LicenseController(LicenseService licenseService, DemoService demoService) {
        this.licenseService = licenseService;
        this.demoService = demoService;
    }

    @GetMapping(path = "/demo")
    public ResponseEntity<DemoInfoDTO> checkDemo(
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to check demo with workspace id: {}", workspaceID);
        return ResponseEntity.ok(this.demoService.getDemoInfo(workspaceID));
    }

    @PostMapping(path = "/demo")
    public ResponseEntity<GenericResponseDTO> startDemo(
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to create demo with workspace id: {}", workspaceID);
        this.demoService.createDemo(workspaceID);
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .message("Demo has been created")
                        .success(true)
                        .build()
        );
    }

    @PostMapping(path = "/license")
    public ResponseEntity<GenericResponseDTO> updateLicenseCredentials(
            @PathVariable("workspaceID") String workspaceID,
            @Valid @RequestBody LicenseDTO body
    ) {
        log.debug("call to update workspace={} license", workspaceID);
        this.licenseService.saveLicense(workspaceID, body);
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .success(true)
                        .message("Workspace license credentials have been updated")
                        .build()
        );
    }
}
