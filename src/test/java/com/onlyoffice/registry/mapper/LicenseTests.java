package com.onlyoffice.registry.mapper;

import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.model.embeddable.LicenseCredentials;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LicenseTests {
    private final String url = "https://example.com";
    private final String header = "Auth";
    private final String secret = "secret";

    @Test
    public void shouldConvertToDTO() {
        LicenseCredentials credentials = LicenseCredentials
                .builder()
                .url(url)
                .secret(secret)
                .header(header)
                .build();

        LicenseDTO licenseDTO = LicenseMapper.INSTANCE.toDTO(credentials);

        assertEquals(credentials.getHeader(), licenseDTO.getServerHeader());
        assertEquals(credentials.getSecret(), licenseDTO.getServerSecret());
        assertEquals(credentials.getUrl(), licenseDTO.getServerUrl());
    }

    @Test
    public void shouldConvertToEntity() {
        LicenseDTO licenseDTO = LicenseDTO
                .builder()
                .serverHeader(header)
                .serverSecret(secret)
                .serverUrl(url)
                .build();

        LicenseCredentials credentials = LicenseMapper.INSTANCE.toEntity(licenseDTO);

        assertEquals(credentials.getHeader(), licenseDTO.getServerHeader());
        assertEquals(credentials.getSecret(), licenseDTO.getServerSecret());
        assertEquals(credentials.getUrl(), licenseDTO.getServerUrl());
    }
}
