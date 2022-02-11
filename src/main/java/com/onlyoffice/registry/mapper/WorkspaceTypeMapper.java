package com.onlyoffice.registry.mapper;

import com.onlyoffice.registry.dto.WorkspaceTypeDTO;
import com.onlyoffice.registry.model.WorkspaceType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface WorkspaceTypeMapper {
    WorkspaceTypeMapper INSTANCE = Mappers.getMapper(WorkspaceTypeMapper.class);

    WorkspaceTypeDTO toDTO(WorkspaceType workspaceType);
    WorkspaceType toEntity(WorkspaceTypeDTO workspaceTypeDTO);
    List<WorkspaceTypeDTO> toListDTO(List<WorkspaceType> workspaceTypes);
    List<WorkspaceType> toListEntity(List<WorkspaceTypeDTO> workspaceTypeDTOS);
}
