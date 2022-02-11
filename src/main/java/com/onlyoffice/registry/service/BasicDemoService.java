package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.DemoInfoDTO;
import com.onlyoffice.registry.model.Demo;
import com.onlyoffice.registry.repository.DemoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class BasicDemoService implements DemoService {
    private DemoRepository demoRepository;
    private BasicWorkspaceService basicWorkspaceService;
    @Autowired
    public BasicDemoService(DemoRepository demoRepository, BasicWorkspaceService basicWorkspaceService) {
        this.demoRepository = demoRepository;
        this.basicWorkspaceService = basicWorkspaceService;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 4)
    public DemoInfoDTO getDemoInfo(String workspaceID) {
        log.debug("trying to get demo info: {}", workspaceID);
        Optional<Demo> demo = this.demoRepository.findById(workspaceID);
        if (demo.isPresent()) {
            log.debug("demo info {} exists", workspaceID);
            return DemoInfoDTO
                    .builder()
                    .hasDemo(true)
                    .isExpired(LocalDateTime.now().compareTo(demo.get().getExpiresAt()) > 0)
                    .build();
        }
        log.debug("demo info {} does not exist. Returning a new info instance");
        return DemoInfoDTO
                .builder()
                .build();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 4)
    public Demo createDemo(String workspaceID) {
        boolean isAlreadyCreated = this.demoRepository
                .findById(workspaceID)
                .isPresent();
        if (isAlreadyCreated) throw new RuntimeException("Could not create: demo already exists");
        log.debug("creating a new demo: {}", workspaceID);
        Demo demo = Demo
                .builder()
                .id(workspaceID)
                .build();
        this.demoRepository.save(demo);
        return demo;
    }
}
