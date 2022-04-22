package com.onlyoffice.registry.service;

import com.onlyoffice.registry.dto.DemoInfoDTO;
import com.onlyoffice.registry.model.Demo;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import com.onlyoffice.registry.repository.DemoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicDemoService implements DemoService {
    private final DemoRepository demoRepository;

    @Transactional(
            isolation = Isolation.READ_UNCOMMITTED,
            rollbackFor = {TransactionException.class},
            timeout = 2
    )
    @Cacheable("demo")
    public DemoInfoDTO getDemoInfo(WorkspaceID id) {
        log.debug("trying to get demo info for workspace of type: {} with id: {}", id.getWorkspaceType(), id.getWorkspaceId());
        Optional<Demo> demo = this.demoRepository.findById(id);
        if (demo.isPresent()) {
            log.debug("demo info {}-{} exists", id.getWorkspaceType(), id.getWorkspaceId());
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

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = {TransactionException.class, RuntimeException.class},
            timeout = 2
    )
    @CacheEvict("demo")
    public Demo createDemo(WorkspaceID id) {
        log.debug("trying to create a new demo: {}-{}", id.getWorkspaceType(), id.getWorkspaceId());
        Demo demo = Demo
                .builder()
                .id(id)
                .build();
        try {
            this.demoRepository.save(demo);
            return demo;
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not create: demo already exists");
        }
    }
}
