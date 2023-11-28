package com.mkr.server.repositories;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.DataStoreCollection;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    @Autowired
    private DataStore dataStore;

    @NotNull
    private DataStoreCollection<User> userCollection() {
        return dataStore.getCollection(DataStoreConfig.users);
    }

    public Optional<User> findUserByEmail(@NotNull String email) {
        try (var data = userCollection().data()) {
            return data.filter(u -> u.getEmail().equals(email)).findFirst();
        }
    }
}
