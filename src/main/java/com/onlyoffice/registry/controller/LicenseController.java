package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.DemoInfoDTO;
import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.service.DemoService;
import com.onlyoffice.registry.service.LicenseService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "v1/workspace/{workspaceTypeName}/{workspaceID}")
@RateLimiter(name = "registryLimiter")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@AllArgsConstructor
@Slf4j
public class LicenseController {
    private final LicenseService licenseService;
    private final DemoService demoService;

    @GetMapping(path = "/demo")
    public ResponseEntity<DemoInfoDTO> checkDemo(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to check demo with workspace id: {}", workspaceID);
        return ResponseEntity.ok(this.demoService
                .getDemoInfo(new WorkspaceID(workspaceID, workspaceTypeName))
        );
    }

    @PostMapping(path = "/demo")
    public ResponseEntity<GenericResponseDTO> startDemo(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to create demo with workspace id: {}", workspaceID);
        this.demoService.createDemo(new WorkspaceID(workspaceID, workspaceTypeName));
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
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @Valid @RequestBody LicenseDTO body
    ) {
        log.debug("call to update {} workspace={} license", workspaceTypeName, workspaceID);
        this.licenseService.saveLicense(new WorkspaceID(workspaceID, workspaceTypeName), body);
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .success(true)
                        .message("Workspace license credentials have been updated")
                        .build()
        );
    }
}
