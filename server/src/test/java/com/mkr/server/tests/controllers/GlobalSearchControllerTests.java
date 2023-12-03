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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalSearchControllerTests {
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
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeTest() {
        var products = dataStore.getCollection(DataStoreConfig.products);

        products.delete(p -> true);

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
            )
        );
    }

    @Test
    public void searchFilterDescriptionTest() throws Exception {
        mockMvc.perform(get("/api/searchfilterdesc"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"limitingPriceRange\":{\"start\":100,\"end\":150},\"colorIds\":[\"black\",\"white\"],\"states\":[\"acceptable\",\"new\"]}"));
    }

    @Test
    public void getProductsTest() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"pageCount\":1,\"totalProductCount\":2,\"products\":[{\"id\":0,\"title\":\"123\",\"imageSource\":\"/images/product/0/0\",\"price\":100,\"totalAmount\":5},{\"id\":1,\"title\":\"456\",\"imageSource\":\"/images/product/1/0\",\"price\":150,\"totalAmount\":5}]}"));
    }
}
