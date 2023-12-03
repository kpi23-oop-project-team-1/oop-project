package com.mkr.server.tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkr.datastore.DataStore;
import com.mkr.datastore.inMemory.InMemoryDataStore;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.*;
import com.mkr.server.dto.ConciseProduct;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Predicate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTests {
    @TestConfiguration
    public static class Configuration {
        @Bean
        @Primary
        public DataStore testDataStore() {
            return new InMemoryDataStore(DataStoreConfig.configuration);
        }
    }

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

        users.delete(p -> true);
        products.delete(p -> true);

        var user = new CustomerTraderUser(
            0,
            "mail@gmail.com",
            passwordEncoder.encode("password")
        );
        user.setTelNumber("1234567891");
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setDisplayName("Display name");
        user.setProfileDescription("Description");
        user.setComments(new Comment[]{
            new Comment(0, 0, 0, 5, "123", 100)
        });
        user.setCartProducts(new CartProduct[]{
            new CartProduct(0, 5)
        });
        user.setProducts(new Product[0]);

        users.insert(user);

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
                1, 1,
                "456",
                150,
                5,
                ProductCategory.SPORT,
                ProductState.ACCEPTABLE,
                ProductStatus.ACTIVE,
                ColorId.WHITE
            ),
            new Product(
                2, 1,
                "222",
                150,
                5,
                ProductCategory.SPORT,
                ProductState.ACCEPTABLE,
                ProductStatus.WAITING_FOR_MODERATION,
                ColorId.WHITE
            ),
            new Product(
                3, 1,
                "333",
                150,
                5,
                ProductCategory.SPORT,
                ProductState.ACCEPTABLE,
                ProductStatus.WAITING_FOR_MODERATION,
                ColorId.WHITE
            )
        );
    }

    @Test
    public void changeProductStatusTest() throws Exception {
        mockMvc.perform(
            post("/api/changeproductstatus")
                .param("id", "2")
                .param("status", "active")
                .with(user("admin@gmail.com").password("password").roles("ADMIN"))
            )
            .andExpect(status().isOk());

        Product product = findFirstProductBy(p -> p.getProductId() == 0);
        Assertions.assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    public void getProductsWaitingApprovalTest() throws Exception {
        var expectedProducts = new ConciseProduct[] {
            new ConciseProduct(2, "222", "/images/product/2/0", 150, 5),
            new ConciseProduct(3, "333", "/images/product/3/0", 150, 5)
        };
        String expectedProductsStr = jacksonObjectMapper.writer().writeValueAsString(expectedProducts);

        mockMvc.perform(
                get("/api/getproductswaitingapproval")
                    .with(user("admin@gmail.com").password("password").roles("ADMIN"))
            )
            .andExpect(status().isOk())
            .andExpect(content().json(expectedProductsStr));
    }

    public Product findFirstProductBy(@NotNull Predicate<Product> predicate) {
        try (var data = dataStore.getCollection(DataStoreConfig.products).data()) {
            return data.filter(predicate).findFirst().orElseThrow();
        }
    }
}
