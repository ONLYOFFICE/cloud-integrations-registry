package com.onlyoffice.registry.model;

import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Workspace {
    @EmbeddedId
    private WorkspaceID id;
    @OneToOne(
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            optional = false
    )
    @JoinColumn(name = "license_id")
    private License license;
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
