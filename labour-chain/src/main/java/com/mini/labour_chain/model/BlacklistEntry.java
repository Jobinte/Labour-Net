package com.mini.labour_chain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklist")
public class BlacklistEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // WORKER or AGENCY
    @Column(nullable = false, length = 20)
    private String type;

    // For worker -> aadharNumber, for agency -> licenseNumber
    @Column(name = "id_value", nullable = false, unique = true, length = 64)
    private String idValue;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIdValue() { return idValue; }
    public void setIdValue(String idValue) { this.idValue = idValue; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
