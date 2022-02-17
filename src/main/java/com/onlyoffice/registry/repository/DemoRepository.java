package com.onlyoffice.registry.repository;

import com.onlyoffice.registry.model.Demo;
import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import org.springframework.data.repository.CrudRepository;

public interface DemoRepository extends CrudRepository<Demo, WorkspaceID> {
}
