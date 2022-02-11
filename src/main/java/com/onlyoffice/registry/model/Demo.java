package com.onlyoffice.registry.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "demo_license")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demo implements Serializable {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusDays(2);
        this.createdAt = now;
        this.expiresAt = expiration;
    }
}
