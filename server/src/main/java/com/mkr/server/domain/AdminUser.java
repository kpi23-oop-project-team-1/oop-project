package com.mkr.server.domain;

import com.mkr.datastore.InheritedModel;

@InheritedModel(id = "admin")
public class AdminUser extends User {
    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }
}
