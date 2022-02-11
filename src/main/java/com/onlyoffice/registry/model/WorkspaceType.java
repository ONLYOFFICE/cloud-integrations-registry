package com.onlyoffice.registry.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "workspace_types")
public class WorkspaceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true)
    private String name;
    private LocalDateTime createdAt;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "type")
    private List<Workspace> workspaces;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
