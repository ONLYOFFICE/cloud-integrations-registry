package com.onlyoffice.registry.mapper;

import com.onlyoffice.registry.dto.LicenseDTO;
import com.onlyoffice.registry.model.embeddable.LicenseCredentials;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LicenseMapper {
    LicenseMapper INSTANCE = Mappers.getMapper(LicenseMapper.class);

    @Mapping(source = "license.url", target = "serverUrl")
    @Mapping(source = "license.header", target = "serverHeader")
    @Mapping(source = "license.secret", target = "serverSecret")
    LicenseDTO toDTO(LicenseCredentials license);

    @Mapping(source = "license.serverUrl", target = "url")
    @Mapping(source = "license.serverHeader", target = "header")
    @Mapping(source = "license.serverSecret", target = "secret")
    LicenseCredentials toEntity(LicenseDTO license);
}
