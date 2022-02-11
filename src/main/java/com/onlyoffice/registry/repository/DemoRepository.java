package com.onlyoffice.registry.repository;

import com.onlyoffice.registry.model.Demo;
import org.springframework.data.repository.CrudRepository;

public interface DemoRepository extends CrudRepository<Demo, String> {
}
