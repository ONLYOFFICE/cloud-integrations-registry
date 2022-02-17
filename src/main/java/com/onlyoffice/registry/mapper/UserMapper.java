package com.onlyoffice.registry.mapper;

import com.onlyoffice.registry.dto.UserDTO;
import com.onlyoffice.registry.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "user.userId", target = "id")
    UserDTO toDto(User user);

    @Mapping(source = "user.id", target = "userId")
    User toEntity(UserDTO user);
}
