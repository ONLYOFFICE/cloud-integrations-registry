package com.onlyoffice.registry.controller;

import com.onlyoffice.registry.dto.DemoInfoDTO;
import com.onlyoffice.registry.dto.GenericResponseDTO;
import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.mapper.WorkspaceMapper;
import com.onlyoffice.registry.service.DemoService;
import com.onlyoffice.registry.service.LicenseService;
import com.onlyoffice.registry.service.WorkspaceService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// TODO: Get stream of workspaces by type id
@RestController
@RequestMapping(path = "v1/workspace/{workspaceTypeName}")
@RateLimiter(name = "registryLimiter")
@PreAuthorize("hasRole(#workspaceTypeName) or hasRole(@DynamicRoles.getRootRole())")
@Slf4j
public class WorkspaceController {
    private WorkspaceService workspaceService;
    private LicenseService licenseService;
    private DemoService demoService;
    @Autowired
    public WorkspaceController(WorkspaceService workspaceService, LicenseService licenseService, DemoService demoService) {
        this.workspaceService = workspaceService;
        this.licenseService = licenseService;
        this.demoService = demoService;
    }

    @GetMapping(path = "/{workspaceID}/demo")
    public ResponseEntity<DemoInfoDTO> checkDemo(
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to check demo with workspace id: {}", workspaceID);
        return ResponseEntity.ok(this.demoService.getDemoInfo(workspaceID));
    }
    
    @PostMapping(path = "/{workspaceID}/demo")
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

    @GetMapping(path = "/{workspaceID}")
    public ResponseEntity<WorkspaceDTO> getWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to get workspace of type: {} with id: {}", workspaceTypeName, workspaceID);
        WorkspaceDTO workspaceDTO = WorkspaceMapper
                .INSTANCE
                .toDTO(this.workspaceService.getWorkspace(workspaceID));

        workspaceDTO.add(
                linkTo(methodOn(WorkspaceController.class).getWorkspace(workspaceTypeName, workspaceID))
                        .withSelfRel(),
                linkTo(methodOn(WorkspaceController.class).saveWorkspace(workspaceTypeName, workspaceDTO))
                        .withRel("save"),
                linkTo(methodOn(WorkspaceController.class).deleteWorkspace(workspaceTypeName, workspaceID))
                        .withRel("delete")
        );

        return ResponseEntity.ok(workspaceDTO);
    }

    @PostMapping(path = "/{workspaceID}/license")
    public ResponseEntity<GenericResponseDTO> updateLicenseCredentials(
            @PathVariable("workspaceID") String workspaceID,
            @Valid @RequestBody WorkspaceDTO body
    ) {
        log.debug("call to update workspace={} license", workspaceID);
        if (!body.getId().equals(workspaceID))
            throw new RuntimeException("Could not save: mismatching id parameters to update license");
        this.licenseService.saveLicense(body);
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .success(true)
                        .message("Workspace license credentials have been updated")
                        .build()
        );
    }
    
    @PostMapping
    public ResponseEntity<WorkspaceDTO> saveWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @Valid @RequestBody WorkspaceDTO body
    ) {
        log.debug("call to create workspace of type: {} with id: {}", workspaceTypeName, body.getId());
        WorkspaceDTO savedWorkspace = this.workspaceService.saveWorkspace(workspaceTypeName, body);

        savedWorkspace.add(
                linkTo(methodOn(WorkspaceController.class).getWorkspace(workspaceTypeName, savedWorkspace.getId()))
                        .withRel("get"),
                linkTo(methodOn(WorkspaceController.class).saveWorkspace(workspaceTypeName, savedWorkspace))
                        .withSelfRel(),
                linkTo(methodOn(WorkspaceController.class).deleteWorkspace(workspaceTypeName, savedWorkspace.getId()))
                        .withRel("delete")
        );

        return ResponseEntity.ok(savedWorkspace);
    }

    @DeleteMapping(path = "/{workspaceID}")
    public ResponseEntity<GenericResponseDTO> deleteWorkspace(
            @PathVariable("workspaceTypeName") String workspaceTypeName,
            @PathVariable("workspaceID") String workspaceID
    ) {
        log.debug("call to delete workspace of type: {} with id: {}", workspaceTypeName, workspaceID);
        this.workspaceService.deleteWorkspace(workspaceID);
        return ResponseEntity.ok(
                GenericResponseDTO
                        .builder()
                        .success(true)
                        .message("Workspace has been deleted")
                        .build()
        );
    }
}
