package com.mkr.server.services;

import com.mkr.server.domain.*;
import com.mkr.server.dto.*;
import com.mkr.server.repositories.ProductRepository;
import com.mkr.server.repositories.UserRepository;
import com.mkr.server.services.storage.StorageService;
import com.mkr.server.services.storage.UserPfpUrlMapper;
import com.mkr.server.utils.DataValidations;
import jakarta.validation.ValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CommentService commentService;

    @Autowired
    @Qualifier("userPfpStorageService")
    private StorageService storageService;

    @Autowired
    private UserPfpUrlMapper pfpUrlMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findUserByEmail(@NotNull String email) {
        return userRepo.findUserByEmail(email);
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

        if (userRepo.findUserByEmail(email).isPresent()) {
            throw new ValidationException("A user with such email already exists");
        }

        if (userRepo.findUserByTelNumber(telNumber).isPresent()) {
            throw new ValidationException("A user with such tel-number already exists");
        }

        String passwordHash = passwordEncoder.encode(password);

        var user = new CustomerTraderUser(0, email, passwordHash);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDisplayName(firstName + " " + lastName);

        user.setTelNumber(telNumber);
        user.setProfileDescription("");
        user.setComments(new Integer[0]);
        user.setCartProducts(new CartProduct[0]);

        userRepo.addUser(user);
    }

    public int getUserIdByEmail(@NotNull String email) {
        return userRepo.findUserByEmail(email).map(User::getId).orElse(-1);
    }

    public Optional<ConciseUserInfo> getConciseUserInfo(int userId) {
        return userRepo.findCustomerTraderUserBy(u -> u.getId() == userId)
            .map(u -> new ConciseUserInfo(u.getId(), u.getDisplayName()));
    }

    public Optional<DetailedUserInfo> getDetailedUserInfo(int id) {
        return userRepo.findCustomerTraderUserBy(u -> u.getId() == id)
            .map(this::getDetailedUserInfo);
    }

    @NotNull
    private DetailedUserInfo getDetailedUserInfo(@NotNull CustomerTraderUser user) {
        var pfpSource = pfpUrlMapper.getEndpointUrl(user.getId());

        return new DetailedUserInfo(
            pfpSource,
            user.getDisplayName(),
            user.getProfileDescription(),
            getUserComments(user)
        );
    }

    public CommentInfo[] getUserComments(@NotNull CustomerTraderUser user) {
        System.out.println("COMMENTS " + Arrays.toString(user.getComments()));

        return Arrays.stream(user.getComments())
            .map(i -> commentService.getCommentInfo(i, this::getConciseUserInfo))
            .toArray(CommentInfo[]::new);
    }

    public void addUserComment(int userId, int authorId, int rating, @NotNull String text) {
        int commentId = commentService.addComment(authorId, rating, text);

        userRepo.addUserComment(userId, commentId);
    }

    public int getUserId(String email) {
        return userRepo.findUserByEmail(email).map(User::getId).orElseThrow();
    }

    public Optional<AccountInfo> getAccountInfo(int id) {
        var pfpSource = pfpUrlMapper.getEndpointUrl(id);

        return userRepo.findCustomerTraderUserBy(u -> u.getId() == id).map(user -> new AccountInfo(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            pfpSource,
            user.getProfileDescription(),
            user.getFirstName(),
            user.getLastName(),
            user.getTelNumber()
        ));
    }

    public void updateAccountInfo(
        @NotNull String email,
        @NotNull UpdateAccountInfo accountInfo,
        @Nullable MultipartFile pfpFile
    ) {
        int userId = userRepo.findCustomerTraderUserByEmail(email).map(User::getId).orElseThrow();

        String passwordHash = null;

        if (accountInfo.password() != null) {
            passwordHash = passwordEncoder.encode(accountInfo.password());
        }

        if (pfpFile != null) {
            storageService.store(pfpFile, getImageFileName(userId));
        }

        userRepo.updateUserInfo(
            userId,
            passwordHash,
            accountInfo.firstName(),
            accountInfo.lastName(),
            accountInfo.aboutMe(),
            accountInfo.username(),
            accountInfo.telNumber()
        );
    }

    @Nullable
    public Resource getImageResource(int id) {
        Resource resource = storageService.getFileResource(getImageFileName(id));

        if (resource.exists() && resource.isReadable()) {
            return resource;
        }

        return null;
    }

    public void checkout(@NotNull String email) {
        CustomerTraderUser user = userRepo.findCustomerTraderUserByEmail(email).orElseThrow();
        for (CartProduct cartProduct : user.getCartProducts()) {
            Product product = productRepo.getProductById(cartProduct.getProductId()).orElseThrow();
            if (cartProduct.getQuantity() > product.getAmount()) {
                throw new ValidationException("Cannot buy more than the trader have");
            }

            productRepo.updateProductAmount(product.getProductId(), a -> a - cartProduct.getQuantity());
        }

        userRepo.removeAllCartProducts(user.getId());
    }

    @NotNull
    private String getImageFileName(int id) {
        return id + ".png";
    }
}
