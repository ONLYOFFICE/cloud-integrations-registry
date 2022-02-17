package com.onlyoffice.registry.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
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
@Table(name = "workspace_user", uniqueConstraints = {
        @UniqueConstraint(
                name = "workspace_user_id",
                columnNames = { "user_id", "workspace_id", "workspace_type" }
        )
})
public class User {
    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String token;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "workspace_id", referencedColumnName = "workspace_id"),
            @JoinColumn(name = "workspace_type", referencedColumnName = "workspace_type")
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
