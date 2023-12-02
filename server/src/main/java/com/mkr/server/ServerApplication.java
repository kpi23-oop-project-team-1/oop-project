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
		user.setComments(new Comment[] {
			new Comment(0, 0, 0, 5, "123", 100)
		});
		user.setCartProducts(new CartProduct[]{
			new CartProduct(1, 5)
		});
		user.setProducts(new Product[0]);

		var admin = new AdminUser(
			1,
			"admin@gmail.com",
			passwordEncoder.encode("password")
		);

		users.insert(user, admin);

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

		return dataStore;
	}
}
