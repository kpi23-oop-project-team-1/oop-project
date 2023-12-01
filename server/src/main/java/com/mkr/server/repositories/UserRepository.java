package com.mkr.server.repositories;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.DataStoreCollection;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.Comment;
import com.mkr.server.domain.CustomerTraderUser;
import com.mkr.server.domain.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Predicate;

@Repository
public class UserRepository {
    @Autowired
    private DataStore dataStore;

    @NotNull
    private DataStoreCollection<User> userCollection() {
        return dataStore.getCollection(DataStoreConfig.users);
    }

    public Optional<User> findFirstUserBy(@NotNull Predicate<User> predicate) {
        try (var data = userCollection().data()) {
            return data.filter(predicate).findFirst();
        }
    }

    public Optional<User> findUserByEmail(@NotNull String email) {
        return findFirstUserBy(u -> u.getEmail().equals(email));
    }

    public Optional<CustomerTraderUser> findCustomerTraderUserBy(@NotNull Predicate<User> predicate) {
        return findFirstUserBy(predicate)
            .filter(u -> u instanceof CustomerTraderUser)
            .map(u -> (CustomerTraderUser)u);
    }

    public Optional<CustomerTraderUser> findCustomerTraderUserByEmail(@NotNull String email) {
        return findCustomerTraderUserBy(u -> u.getEmail().equals(email));
    }

    public Optional<User> findUserByTelNumber(@NotNull String telNumber) {
        return findFirstUserBy(u ->
            u instanceof CustomerTraderUser ctUser && ctUser.getTelNumber().equals(telNumber)
        );
    }

    public void addUser(@NotNull User user) {
        userCollection().insert(user);
    }

    public void addComment(Comment comment) {
        userCollection().update(
            u -> u.getId() == comment.getTargetId() && u instanceof CustomerTraderUser,
            u -> ((CustomerTraderUser)u).withComment(comment)
        );
    }

    public void updateUserInfo(
        int userId,
        String passwordHash,
        String firstName,
        String lastName,
        String telNumber
    ) {
        userCollection().update(
            u -> u.getId() == userId && u instanceof CustomerTraderUser,
            u -> ((CustomerTraderUser)u).withPersonalInfo(passwordHash, firstName, lastName, telNumber)
        );
    }
}
