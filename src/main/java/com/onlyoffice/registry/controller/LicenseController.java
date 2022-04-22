package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.DemoInfoDTO;
import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.service.DemoService;
import com.onlyoffice.registry.service.LicenseService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "v1/workspace/{workspaceTypeName}/{workspaceID}")
@RateLimiter(name = "registryRateLimiter")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@RequiredArgsConstructor
@Slf4j
public class LicenseController {
    private final LicenseService licenseService;
    private final DemoService demoService;

    @GetMapping(path = "/demo")
    @TimeLimiter(name = "queryTimeoutLimiter", fallbackMethod = "checkDemoFallback")
    @Cacheable("demo")
    public CompletableFuture<ResponseEntity<DemoInfoDTO>> checkDemo(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to check demo with workspace id: {}", workspaceID);

        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(this.demoService
                    .getDemoInfo(new WorkspaceID(workspaceID, workspaceTypeName))
        ));
    }

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> checkDemoFallback(
            String workspaceTypeName,
            String workspaceID,
            TimeoutException rnp
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.internalServerError()
                .body(GenericResponseDTO.builder()
                        .success(false)
                        .message(workspaceTypeName + " demo fetching timeout: " + workspaceID)
                        .build()
                )
        );
    }

    @PostMapping(path = "/demo")
    public CompletableFuture<ResponseEntity<GenericResponseDTO>> startDemo(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to create demo with workspace id: {}", workspaceID);

        return CompletableFuture.supplyAsync(() -> {
            this.demoService.createDemo(new WorkspaceID(workspaceID, workspaceTypeName));
            return ResponseEntity.ok(
                    GenericResponseDTO
                            .builder()
                            .message("Demo has been created")
                            .success(true)
                            .build()
            );
        });
    }

    @PostMapping(path = "/license")
    public CompletableFuture<ResponseEntity<GenericResponseDTO>> updateLicenseCredentials(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @Valid @RequestBody LicenseDTO body
    ) {
        log.debug("call to update {} workspace={} license", workspaceTypeName, workspaceID);

        return CompletableFuture.supplyAsync(() -> {
            this.licenseService.saveLicense(new WorkspaceID(workspaceID, workspaceTypeName), body);
            return ResponseEntity.ok(
                    GenericResponseDTO
                            .builder()
                            .success(true)
                            .message("Workspace license credentials have been updated")
                            .build()
            );
        });
    }
}
