package com.task.harbor.domain.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {

    @Id
    private UUID id;

    @Column("email")
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("totp_secret")
    private String totpSecret;

    @Column("role")
    private String role;

    @Column("attributes")
    @Transient
    private Map<String, Object> attributes = new HashMap<>();

    @Column("tenant_id")
    private UUID tenantId;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    public String getAttributesJson() {
        return attributes.isEmpty() ? "{}" :
                new ObjectMapper().valueToTree(attributes).toString();
    }

    public void setAttributesJson(String json) {
        try {
            this.attributes = json == null || json.isEmpty()
                    ? new HashMap<>() :
                    new ObjectMapper().readValue(json, Map.class);
        } catch (Exception e) {
            this.attributes = new HashMap<>();
        }
    }
}
