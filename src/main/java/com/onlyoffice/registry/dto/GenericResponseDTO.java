package com.onlyoffice.registry.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericResponseDTO {
    private Boolean success;
    private String message;
}
