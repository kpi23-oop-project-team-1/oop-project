package com.mkr.server.domain;

import com.mkr.datastore.BaseModel;

@BaseModel(inheritedClasses = { CustomerTraderUser.class, AdminUser.class })
public abstract class User {
    private int id;

    private String email;
    private String passwordHash;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public abstract UserRole getRole();
}
