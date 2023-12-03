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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTests {
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
        user.setComments(new Integer[] { 0 });
        user.setCartProducts(new CartProduct[]{
            new CartProduct(0, 5)
        });

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
    }

    @Test
    public void cartProductsTest() throws Exception {
        mockMvc.perform(get("/api/cartproducts").with(user("mail@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER")))
            .andExpect(status().isOk())
            .andExpect(content().json("[{\"id\":0,\"title\":\"123\",\"imageSource\":\"/images/product/0/0\",\"price\":100,\"quantity\":5,\"totalAmount\":5}]"));
    }

    @Test
    public void cartProductsUnauthorizedTest() throws Exception {
        mockMvc.perform(get("/api/cartproducts")).andExpect(status().isUnauthorized());
    }

    @Test
    public void addCartProductTest() throws Exception {
        mockMvc.perform(
                post("/api/addcartproduct?id=1")
                    .with(user("mail@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER"))
            )
            .andExpect(status().isOk());

        CartProduct[] cart = getZeroUserCart();
        var expectedCart = new CartProduct[]{
            new CartProduct(0, 5),
            new CartProduct(1, 1)
        };

        assertArrayEquals(expectedCart, cart);
    }

    @Test
    public void changeCartProductAmountTest() throws Exception {
        mockMvc.perform(
                post("/api/cartproductamount?id=0&amount=2")
                    .with(user("mail@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER"))
            )
            .andExpect(status().isOk());

        CartProduct[] cart = getZeroUserCart();
        var expectedCart = new CartProduct[]{
            new CartProduct(0, 2)
        };

        assertArrayEquals(expectedCart, cart);
    }

    @Test
    public void deleteCartProductTest() throws Exception {
        mockMvc.perform(
                delete("/api/cartproduct?id=0")
                    .with(user("mail@gmail.com").password("password").roles("USER", "CUSTOMER_TRADER"))
            )
            .andExpect(status().isOk());

        CartProduct[] cart = getZeroUserCart();
        assertEquals(0, cart.length);
    }

    private CartProduct[] getZeroUserCart() {
        try (var data = dataStore.getCollection(DataStoreConfig.users).data()) {
            var firstUser = data.filter(u -> u.getId() == 0 && u instanceof CustomerTraderUser)
                .map(u -> (CustomerTraderUser) u)
                .findFirst()
                .orElseThrow();

            return firstUser.getCartProducts();
        }
    }
}
