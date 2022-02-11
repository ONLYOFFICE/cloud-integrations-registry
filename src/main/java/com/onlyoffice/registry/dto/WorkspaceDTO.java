package com.onlyoffice.registry.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Pattern;

@Data
@Builder
public class WorkspaceDTO extends RepresentationModel<WorkspaceDTO> {
    @Length(min = 4, max = 200)
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]*$")
    private String id;
    private String serverUrl;
    private String serverHeader;
    private String serverSecret;
}
