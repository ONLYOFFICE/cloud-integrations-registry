package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.DemoInfoDTO;
import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.service.DemoService;
import com.onlyoffice.registry.service.LicenseService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "v1/workspace/{workspaceTypeName}/{workspaceID}")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@RequiredArgsConstructor
@Slf4j
public class LicenseController {
    private final LicenseService licenseService;
    private final DemoService demoService;

    @GetMapping(path = "/demo")
    @RateLimiter(name = "queryRateLimiter", fallbackMethod = "checkDemoRateFallback")
    @TimeLimiter(name = "queryTimeoutLimiter", fallbackMethod = "checkDemoTimeoutFallback")
    public CompletableFuture<ResponseEntity<DemoInfoDTO>> checkDemo(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to check demo with workspace id: {}", workspaceID);
            return ResponseEntity.ok(this.demoService
                    .getDemoInfo(new WorkspaceID(workspaceID, workspaceTypeName))
            );
        });
    }

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> checkDemoRateFallback(
            String workspaceTypeName,
            String workspaceID,
            RequestNotPermitted e
    ) {
        log.warn("check demo rate {}({}) - {}", workspaceID, workspaceTypeName, e.getMessage());
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                        GenericResponseDTO.builder()
                                .success(false)
                                .message(e.getMessage())
                                .build(),
                        HttpStatus.SERVICE_UNAVAILABLE));
    }

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> checkDemoTimeoutFallback(
            String workspaceTypeName,
            String workspaceID,
            TimeoutException e
    ) {
        log.warn("check demo timeout {}({}) - {}", workspaceID, workspaceTypeName, e.getMessage());
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }

    @PostMapping(path = "/demo")
    @RateLimiter(name = "commandRateLimiter", fallbackMethod = "createDemoFallback")
    public CompletableFuture<ResponseEntity<GenericResponseDTO>> startDemo(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to create demo with workspace id: {}", workspaceID);
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

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> createDemoFallback(
            String workspaceTypeName,
            String workspaceID,
            RequestNotPermitted e
    ) {
        log.warn("create demo {}({}) - {}", workspaceID, workspaceTypeName, e.getMessage());
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }

    @PostMapping(path = "/license")
    @RateLimiter(name = "commandRateLimiter", fallbackMethod = "updateLicenseFallback")
    @CacheEvict(value = "workspaces", key = "{#workspaceTypeName, #workspaceID}")
    public CompletableFuture<ResponseEntity<GenericResponseDTO>> updateLicenseCredentials(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID,
            @Valid @RequestBody LicenseDTO body
    ) {
        return CompletableFuture.supplyAsync(() -> {
            log.debug("call to update {} workspace={} license", workspaceTypeName, workspaceID);
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

    public CompletableFuture<ResponseEntity<GenericResponseDTO>> updateLicenseFallback(
            String workspaceTypeName,
            String workspaceID,
            LicenseDTO body,
            RequestNotPermitted e
    ) {
        log.warn("update license {}({}) - {}", workspaceID, workspaceTypeName, e.getMessage());
        return CompletableFuture.completedFuture(new ResponseEntity<>(
                GenericResponseDTO.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build(),
                HttpStatus.SERVICE_UNAVAILABLE));
    }
}
