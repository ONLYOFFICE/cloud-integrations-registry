package com.onlyoffice.registry.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
public class UserDTO extends RepresentationModel<UserDTO> {
    @Length(min = 4, max = 200)
    private String id;
    @Length(min = 4, max = 50)
    private String username;
    @Length(min = 4)
    private String token;
}
