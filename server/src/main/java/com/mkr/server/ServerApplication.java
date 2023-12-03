package com.mkr.server;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.inMemory.InMemoryDataStore;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.controllers.AdminController;
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
		var user1 = new CustomerTraderUser(
			0,
			"mail@gmail.com",
			passwordEncoder.encode("password")
		);
		user1.setTelNumber("1234567891");
		user1.setFirstName("First name");
		user1.setLastName("Last name");
		user1.setDisplayName("Display name");
		user1.setProfileDescription("Description");
		user1.setComments(new Comment[] {
			new Comment(0, 0, 0, 5, "123", 100)
		});
		user1.setCartProducts(new CartProduct[]{
			new CartProduct(1, 5)
		});
		user1.setProducts(new Product[0]);

		var user2 = new CustomerTraderUser(
			2,
			"mail2@gmail.com",
			passwordEncoder.encode("password")
		);
		user2.setTelNumber("1234567891");
		user2.setFirstName("First name");
		user2.setLastName("Last name");
		user2.setDisplayName("Display name");
		user2.setProfileDescription("Description");
		user2.setComments(new Comment[] {
			new Comment(0, 0, 2, 5, "123", 100)
		});
		user2.setCartProducts(new CartProduct[]{
			new CartProduct(1, 5)
		});
		user2.setProducts(new Product[0]);

		var admin = new AdminUser(
			1,
			"admin@gmail.com",
			passwordEncoder.encode("password")
		);

		users.insert(user1, user2, admin);
		users.setLastID(2);

		var products = dataStore.getCollection(DataStoreConfig.products);
		products.insert(
			new Product(
				0, 0,
				"123",
				100,
				5,
				ProductCategory.DRESS,
				ProductState.NEW,
				ProductStatus.WAITING_FOR_MODERATION,
				ColorId.BLACK
			),
			new Product(
				1, 2,
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

		return dataStore;
	}
}
