package com.onlyoffice.registry;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.security.oauth2.resourceserver.jwk-set-uri=http://127.0.0.1:9080/auth/realms/ORegistry/protocol/openid-connect/certs",
})
public abstract class RegistryApplicationTests {
}
