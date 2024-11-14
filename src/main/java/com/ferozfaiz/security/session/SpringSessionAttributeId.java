package com.ferozfaiz.security.session;

import java.io.Serializable;
import java.util.Objects;

public class SpringSessionAttributeId implements Serializable {

    private String sessionPrimaryId;
    private String attributeName;

    // Default constructor

    public SpringSessionAttributeId() {}

    // Parameterized constructor

    public SpringSessionAttributeId(String sessionPrimaryId, String attributeName) {
        this.sessionPrimaryId = sessionPrimaryId;
        this.attributeName = attributeName;
    }

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

    // Override equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpringSessionAttributeId that = (SpringSessionAttributeId) o;
        return Objects.equals(sessionPrimaryId, that.sessionPrimaryId) &&
                Objects.equals(attributeName, that.attributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionPrimaryId, attributeName);
    }
}
