package com.ferozfaiz.security.session;

import jakarta.persistence.*;

@Entity
@Table(name = "SPRING_SESSION_ATTRIBUTES")
@IdClass(SpringSessionAttributeId.class)
public class SpringSessionAttribute {
    @Id
    @Column(name = "SESSION_PRIMARY_ID", length = 36, nullable = false)
    private String sessionPrimaryId;

    @Id
    @Column(name = "ATTRIBUTE_NAME", length = 200, nullable = false)
    private String attributeName;

    @Lob
    @Column(name = "ATTRIBUTE_BYTES", nullable = false, columnDefinition = "bytea")
    private byte[] attributeBytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SESSION_PRIMARY_ID", insertable = false, updatable = false)
    private SpringSession springSession;

    // Getters and setters

    public String getSessionPrimaryId() {
        return sessionPrimaryId;
    }

    public void setSessionPrimaryId(String sessionPrimaryId) {
        this.sessionPrimaryId = sessionPrimaryId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public byte[] getAttributeBytes() {
        return attributeBytes;
    }

    public void setAttributeBytes(byte[] attributeBytes) {
        this.attributeBytes = attributeBytes;
    }

    public SpringSession getSpringSession() {
        return springSession;
    }

    public void setSpringSession(SpringSession springSession) {
        this.springSession = springSession;
    }
}
