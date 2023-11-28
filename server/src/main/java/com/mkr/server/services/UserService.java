package com.mkr.server.services;

import com.mkr.server.domain.*;
import com.mkr.server.repositories.UserRepository;
import com.mkr.server.utils.DataValidations;
import jakarta.validation.ValidationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findUserByEmail(@NotNull String email) {
        return repo.findUserByEmail(email);
    }

    public void addCustomerTraderUser(
        @NotNull String email,
        @NotNull String password,
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String telNumber
    ) {
        if (!DataValidations.isValidEmail(email)) {
            throw new ValidationException("Invalid email");
        }

        if (password.length() < 8) {
            throw new ValidationException("Too small password");
        }

        if (firstName.isEmpty()) {
            throw new ValidationException("Empty firstName");
        }

        if (lastName.isEmpty()) {
            throw new ValidationException("Empty lastName");
        }

        if (!DataValidations.isValidTelNumber(telNumber)) {
            throw new ValidationException("Invalid telNumber");
        }

        if (repo.findUserByEmail(email).isPresent()) {
            throw new ValidationException("A user with such email already exists");
        }

        if (repo.findUserByTelNumber(telNumber).isPresent()) {
            throw new ValidationException("A user with such tel-number already exists");
        }

        String passwordHash = passwordEncoder.encode(password);

        var user = new CustomerTraderUser(0, email, passwordHash);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDisplayName(firstName + " " + lastName);

        user.setTelNumber(telNumber);
        user.setProfileDescription("");
        user.setProducts(new Product[0]);
        user.setCartProducts(new CartProduct[0]);
        user.setComments(new Comment[0]);

        repo.addUser(user);
    }
}
