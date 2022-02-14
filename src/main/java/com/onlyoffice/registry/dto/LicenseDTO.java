package com.onlyoffice.registry.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class LicenseDTO {
    @URL(protocol = "https", message = "This protocol is not supported")
    @Length(min = 13, max = 120)
    private String serverUrl;
    @Length(min = 4, max = 50)
    private String serverHeader;
    @Length(min = 4, max = 150)
    private String serverSecret;
}
