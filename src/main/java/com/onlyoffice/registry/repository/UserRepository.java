package com.onlyoffice.registry.repository;

import com.onlyoffice.registry.model.User;
import com.onlyoffice.registry.model.embeddable.UserID;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, UserID> {
}
