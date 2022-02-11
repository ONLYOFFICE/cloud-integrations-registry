package com.onlyoffice.registry.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DemoInfoDTO {
    private boolean hasDemo;
    private boolean isExpired;
}
