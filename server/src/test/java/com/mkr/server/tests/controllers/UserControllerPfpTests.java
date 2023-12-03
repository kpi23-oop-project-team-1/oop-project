package com.mkr.server.tests.controllers;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.inMemory.InMemoryDataStore;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.*;
import com.mkr.server.services.storage.FileStorageService;
import com.mkr.server.services.storage.StorageService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerPfpTests {
    private static final Path testImageDir = Path.of("test_images");

    @TestConfiguration
    public static class UserControllerConfiguration {
        @Primary
        @Bean
        public StorageService userPfpStorageService() {
            return new FileStorageService(testImageDir);
        }
    }

    @Autowired
    private DataStore dataStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StorageService storageService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeTest() {
        var users = dataStore.getCollection(DataStoreConfig.users);
        var products = dataStore.getCollection(DataStoreConfig.products);

        users.delete(p -> true);
        products.delete(p -> true);

        users.insert(createUser(0));
        users.insert(createUser(1));
    }

    @AfterAll
    public static void afterAll() throws IOException {
        FileSystemUtils.deleteRecursively(testImageDir);
        FileSystemUtils.deleteRecursively(TestServerConfiguration.dataStorePath);
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
    public void userPfpTest() throws Exception {
        var bytes = "png".getBytes(StandardCharsets.UTF_8);
        var mockFile = new MockMultipartFile(
            "0.png",
            "0.png",
            null,
            bytes
        );
        storageService.store(mockFile, "0.png");

        mockMvc.perform(get("/images/pfp/0"))
            .andExpect(status().isOk())
            .andExpect(content().bytes(bytes));
    }
    @Test
    public void userPfp404Test() throws Exception {
        mockMvc.perform(get("/images/pfp/123"))
            .andExpect(status().isNotFound());
    }
}
