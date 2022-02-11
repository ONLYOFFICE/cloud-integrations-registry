package com.onlyoffice.registry.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WorkspaceTypeDTO {
    @Length(min = 3, max = 15)
    private String name;
}
