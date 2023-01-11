package com.backend.oauthlogin.entity;

public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),
    ROLE_GUEST("ROLE_GUEST");

    String role;

    Role(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }
}
