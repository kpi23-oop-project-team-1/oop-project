package com.mkr.server.domain;

import com.mkr.datastore.InheritedModel;

@InheritedModel(id = "admin")
public class AdminUser extends User {
    public AdminUser() {
    }

    public AdminUser(int id, String email, String passwordHash) {
        super(id, email, passwordHash);
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }
}
