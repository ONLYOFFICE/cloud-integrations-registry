package com.onlyoffice.registry.model;

import com.onlyoffice.registry.model.embeddable.UserID;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workspace_user")
public class User {
    @EmbeddedId
    private UserID id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String token;
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
