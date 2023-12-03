package com.mkr.server.tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkr.datastore.DataStore;
import com.mkr.datastore.inMemory.InMemoryDataStore;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.*;
import com.mkr.server.dto.AccountInfo;
import com.mkr.server.dto.CommentInfo;
import com.mkr.server.dto.ConciseUserInfo;
import com.mkr.server.dto.DetailedUserInfo;
import com.mkr.server.tests.TestServerConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private DataStore dataStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @BeforeEach
    public void beforeTest() {
        var users = dataStore.getCollection(DataStoreConfig.users);
        var products = dataStore.getCollection(DataStoreConfig.products);
        var comments = dataStore.getCollection(DataStoreConfig.comments);

        users.delete(p -> true);
        products.delete(p -> true);
        comments.delete(p -> true);

        var zeroUser = createUser(0);
        ((CustomerTraderUser)zeroUser).setComments(new Integer[] { 0 });

        users.insert(zeroUser);
        users.insert(createUser(1));

        products.insert(
            new Product(
                0, 0,
                "123",
                100,
                5,
                ProductCategory.DRESS,
                ProductState.NEW,
                ProductStatus.ACTIVE,
                ColorId.BLACK
            ),
            new Product(
                1, 0,
                "456",
                150,
                5,
                ProductCategory.SPORT,
                ProductState.ACCEPTABLE,
                ProductStatus.ACTIVE,
                ColorId.WHITE
            )
        );

        comments.insert(
            new Comment(
                0,
                1,
                5,
                "123 0",
                100L
            )
        );
    }

    @AfterAll
    public static void afterAll() throws IOException {
        //FileSystemUtils.deleteRecursively(TestServerConfiguration.dataStorePath);
    }

    private User createUser(int id) {
        var user = new CustomerTraderUser(
            id,
            "mail" + id + "@gmail.com",
            passwordEncoder.encode("password")
        );
        user.setTelNumber("1234567891");
        user.setFirstName("First name " + id);
        user.setLastName("Last name " + id);
        user.setDisplayName("Display name " + id);
        user.setProfileDescription("Description " + id);

        return user;
    }

    @Test
    public void userTypeTest() throws Exception {
        mockMvc.perform(get("/api/usertype").with(user("mail0@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER")))
            .andExpect(status().isOk())
            .andExpect(content().string("\"customer-trader\""));
    }

    @Test
    public void detailedUserInfoTest() throws Exception {
        var expectedUserInfo = new DetailedUserInfo(
            "/images/pfp/0",
            "Display name 0",
            "Description 0",
            new CommentInfo[] {
                new CommentInfo(
                    0,
                    new ConciseUserInfo(1, "Display name 1"),
                    5,
                    "123 0",
                    LocalDateTime.ofEpochSecond(100, 0, ZoneOffset.UTC)
                )
            }
        );

        var expectedUserInfoStr = jacksonObjectMapper.writer().writeValueAsString(expectedUserInfo);

        mockMvc.perform(get("/api/detaileduserinfo?id=0"))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedUserInfoStr));
    }

    @Test
    public void accountInfoTest() throws Exception {
        var expectedAccountInfo = new AccountInfo(
            0,
            "mail0@gmail.com",
            "Display name 0",
            "/images/pfp/0",
            "Description 0",
            "First name 0",
            "Last name 0",
            "1234567891"
        );

        var expectedAccountInfoStr = jacksonObjectMapper.writer().writeValueAsString(expectedAccountInfo);

        mockMvc.perform(get("/api/accountinfo?id=0"))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedAccountInfoStr));
    }

    @Test
    public void updateAccountInfoTest() throws Exception {
        var newPassword = "password1";

        mockMvc.perform(
            post("/api/updateaccountinfo")
                .param("password", newPassword)
                .param("username", "Display name 2")
                .param("aboutMe", "Description 2")
                .param("firstName", "First name 2")
                .param("lastName", "Last name 2")
                .param("telNumber", "0000000000")
                .with(user("mail0@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER"))
            )
            .andExpect(status().isOk());

        CustomerTraderUser actualUser = getZeroUser();

        assertTrue(passwordEncoder.matches(newPassword, actualUser.getPasswordHash()));
        assertEquals("Display name 2", actualUser.getDisplayName());
        assertEquals("Description 2", actualUser.getProfileDescription());
        assertEquals("First name 2", actualUser.getFirstName());
        assertEquals("Last name 2", actualUser.getLastName());
        assertEquals("0000000000", actualUser.getTelNumber());

    }

    private CustomerTraderUser getZeroUser() {
        try (var data = dataStore.getCollection(DataStoreConfig.users).data()) {
            return data.filter(u -> u.getId() == 0 && u instanceof CustomerTraderUser)
                .map(u -> (CustomerTraderUser) u)
                .findFirst()
                .orElseThrow();
        }
    }
}
