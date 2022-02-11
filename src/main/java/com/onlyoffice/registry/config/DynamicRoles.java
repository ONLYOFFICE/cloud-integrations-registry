package com.onlyoffice.registry.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("DynamicRoles")
@Getter
public class DynamicRoles {
    @Value("${spring.security.oauth2.role.root}")
    private String rootRole;
}
