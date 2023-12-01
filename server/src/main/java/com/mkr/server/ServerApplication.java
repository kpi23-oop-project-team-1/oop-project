package com.mkr.server;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.inMemory.InMemoryDataStore;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.Comment;
import com.mkr.server.domain.CustomerTraderUser;
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
		user.setTelNumber("12345678910");
		user.setPfpSource("");
		user.setDisplayName("Display name");
		user.setProfileDescription("Description");
		user.setComments(new Comment[] {
			new Comment(0, 0, 0, 5, "123", 100)
		});

		users.insert(user);

		return dataStore;
	}
}
