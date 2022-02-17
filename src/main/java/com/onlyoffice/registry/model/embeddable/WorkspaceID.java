package com.onlyoffice.registry.model.embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceID implements Serializable {
    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;
    @Column(name = "workspace_type", nullable = false)
    private String workspaceType;
}
