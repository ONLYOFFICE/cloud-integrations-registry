package com.onlyoffice.registry.model;

import com.onlyoffice.registry.model.embeddable.WorkspaceID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "demo_license")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demo implements Serializable {
    @EmbeddedId
    @Column(updatable = false)
    private WorkspaceID id;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @Column(updatable = false)
    private LocalDateTime expiresAt;
    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusDays(2);
        this.createdAt = now;
        this.expiresAt = expiration;
    }
}
