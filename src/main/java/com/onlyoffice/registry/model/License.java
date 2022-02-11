package com.onlyoffice.registry.model;

import com.onlyoffice.registry.model.embeddable.LicenseCredentials;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "licenses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class License {
    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String id;
    private LicenseCredentials credentials;
    @OneToOne(mappedBy = "license")
    private Workspace workspace;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
