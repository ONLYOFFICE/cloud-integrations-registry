package com.onlyoffice.registry.mapper;

import com.onlyoffice.registry.dto.WorkspaceDTO;
import com.onlyoffice.registry.model.Workspace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WorkspaceMapper {
    WorkspaceMapper INSTANCE = Mappers.getMapper(WorkspaceMapper.class);

    @Mapping(source = "workspace.license.credentials.url", target = "serverUrl")
    @Mapping(source = "workspace.license.credentials.header", target = "serverHeader")
    @Mapping(source = "workspace.license.credentials.secret", target = "serverSecret")
    @Mapping(source = "workspace.id.workspaceId", target = "id")
    WorkspaceDTO toDTO(Workspace workspace);

    @Mapping(source = "workspace.serverUrl", target = "license.credentials.url")
    @Mapping(source = "workspace.serverHeader", target = "license.credentials.header")
    @Mapping(source = "workspace.serverSecret", target = "license.credentials.secret")
    @Mapping(source = "workspace.id", target = "id.workspaceId")
    Workspace toEntity(WorkspaceDTO workspace);
}
