package com.mkr.server;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.inMemory.InMemoryDataStore;
import com.mkr.server.config.DataStoreConfig;
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
		users.insert(
			new CustomerTraderUser(
				0,
				"mail@gmail.com",
				passwordEncoder.encode("password")
			)
		);

		return dataStore;
	}
}
