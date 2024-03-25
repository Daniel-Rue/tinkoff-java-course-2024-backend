package edu.java.scrapper.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(name = "last_check_time", nullable = false)
    private OffsetDateTime lastCheckTime;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @ManyToMany(mappedBy = "links")
    private Set<Chat> chats = new HashSet<>();

    public Link(Long id, String url, OffsetDateTime lastCheckTime, OffsetDateTime createdAt, String createdBy) {
        this.id = id;
        this.url = url;
        this.lastCheckTime = lastCheckTime;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
}
