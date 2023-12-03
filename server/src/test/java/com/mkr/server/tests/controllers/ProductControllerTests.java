package com.mkr.server.tests.controllers;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.inMemory.InMemoryDataStore;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests {
    @TestConfiguration
    public static class Configuration {
        @Bean
        @Primary
        public DataStore testDataStore() {
            return new InMemoryDataStore(DataStoreConfig.configuration);
        }

        @SuppressWarnings("deprecation")
        @Primary
        @Bean
        public PasswordEncoder testPasswordEncoder() {
            return NoOpPasswordEncoder.getInstance();
        }
    }

    @Autowired
    private DataStore dataStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeTest() {
        var users = dataStore.getCollection(DataStoreConfig.users);
        var products = dataStore.getCollection(DataStoreConfig.products);

        users.delete(u -> true);
        products.delete(p -> true);
        users.insert(createUser(0, 1));
        users.insert(createUser(1, -1));

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
        products.setLastID(1);
    }

    private User createUser(int id, int commentAuthorId) {
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

        if (commentAuthorId >= 0) {
            user.setComments(new Comment[]{
                    new Comment(0, id, commentAuthorId, 5, "123 " + id, 100)
            });
        } else {
            user.setComments(new Comment[0]);
        }

        user.setCartProducts(new CartProduct[]{
                new CartProduct(0, 5)
        });
        user.setProducts(new Product[0]);

        return user;
    }

    @Test
    public void productInfoTest() throws Exception {
        mockMvc.perform(get("/api/product?id=0"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":0,\"title\":\"123\",\"imageSources\":[],\"price\":100,\"totalAmount\":5,\"description\":\"\",\"comments\":[],\"category\":\"dress\",\"state\":\"new\",\"color\":\"black\",\"status\":\"active\",\"trader\":{\"id\":0,\"displayName\":\"Display name 0\"}}"));
    }

    @Test
    public void postProductCommentTest() throws Exception {
        mockMvc.perform(
                post("/api/postproductcomment")
                        .param("targetId", "0")
                        .param("rating", "3")
                        .param("text", "test")
                        .with(user("mail1@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER"))
        ).andExpect(status().isOk());

        try (var stream = dataStore.getCollection(DataStoreConfig.products).data()) {
            var product = stream
                .filter(p -> p.getProductId() == 0)
                .findFirst().orElseThrow();

            assertEquals(1, product.getComments().length);
        }
    }

    @Test
    public void addProductTest() throws Exception {
        mockMvc.perform(
                multipart("/api/addproduct")
                        .param("title", "test")
                        .param("description", "desc")
                        .param("price", "100")
                        .param("amount", "150")
                        .param("category", "dress")
                        .param("state", "new")
                        .param("color", "black")
                        .with(user("mail0@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER"))
        ).andExpect(status().isOk());

        var expected = new Product(
                2,
                0,
                "test",
                100,
                150,
                ProductCategory.DRESS,
                ProductState.NEW,
                ProductStatus.WAITING_FOR_MODERATION,
                ColorId.BLACK
        );

        try (var stream = dataStore.getCollection(DataStoreConfig.products).data()) {
            var product = stream
                    .filter(p -> p.getProductId() == 2)
                    .findFirst().orElseThrow();

            assertEquals(expected.hashCode(), product.hashCode());
        }
    }

    @Test
    public void updateProductTest() throws Exception {
        mockMvc.perform(
                multipart("/api/updateproduct")
                        .param("title", "test")
                        .param("description", "desc")
                        .param("price", "100")
                        .param("amount", "150")
                        .param("category", "dress")
                        .param("state", "new")
                        .param("color", "black")
                        .param("id", "1")
                        .with(user("mail0@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER"))
        ).andExpect(status().isOk());

        var expected = new Product(
                1,
                0,
                "test",
                100,
                150,
                ProductCategory.DRESS,
                ProductState.NEW,
                ProductStatus.ACTIVE,
                ColorId.BLACK
        );

        try (var stream = dataStore.getCollection(DataStoreConfig.products).data()) {
            var product = stream
                    .filter(p -> p.getProductId() == 1)
                    .findFirst().orElseThrow();

            assertEquals(expected.hashCode(), product.hashCode());
        }
    }

    @Test
    public void deleteProductsTest() throws Exception {
        mockMvc.perform(
                delete("/api/deleteproducts")
                        .content("[0,1]")
                        .with(user("mail0@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER"))
        ).andExpect(status().isOk());

        try (var stream = dataStore.getCollection(DataStoreConfig.products).data()) {
            assertEquals(0, stream.count());
        }
    }
}
