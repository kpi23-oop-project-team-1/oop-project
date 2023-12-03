package com.mkr.server;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.inMemory.InMemoryDataStore;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ServerApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	public DataStore dataStore(PasswordEncoder passwordEncoder) {
		var dataStore = new InMemoryDataStore(DataStoreConfig.configuration);

		var users = dataStore.getCollection(DataStoreConfig.users);
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
		users.setLastID(0);

		var products = dataStore.getCollection(DataStoreConfig.products);
		var product1 = new Product(
			0,
			0,
			"123",
			100,
			5,
			ProductCategory.DRESS,
			ProductState.NEW,
			ProductStatus.ACTIVE,
			ColorId.BLACK
		);
		product1.setComments(new Integer[0]);

		var product2 = new Product(
			1, 0,
			"456",
			150,
			5,
			ProductCategory.SPORT,
			ProductState.ACCEPTABLE,
			ProductStatus.ACTIVE,
			ColorId.WHITE
		);
		product2.setComments(new Integer[0]);

		products.insert(product1, product2);
		users.setLastID(1);

		var comments = dataStore.getCollection(DataStoreConfig.comments);
		comments.insert(
			new Comment(
				0,
				0,
				4,
				"Example comment",
				100L
			)
		);
		users.setLastID(0);

		return dataStore;
	}
}
