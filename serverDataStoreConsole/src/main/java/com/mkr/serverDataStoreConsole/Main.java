package com.mkr.serverDataStoreConsole;

import com.mkr.datastore.DataStoreCollectionDescriptor;
import com.mkr.datastore.DataStoreCollectionDescriptorBuilder;
import com.mkr.datastore.DataStoreConfiguration;
import com.mkr.datastore.inFile.InFileDataStore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.file.Path;
import java.util.function.Function;

public class Main {
    public static final DataStoreCollectionDescriptor<User> users = new DataStoreCollectionDescriptorBuilder<User>()
        .name("users")
        .entityClass(User.class)
        .build();

    public static final DataStoreConfiguration configuration = DataStoreConfiguration.builder()
        .addCollection(users)
        .build();

    public static void main(String[] args) {
        String command = args[0];

        if(command.equalsIgnoreCase("add-admin")) {
            String dirPath = args[1];
            String email = args[2];
            String password = args[3];

            var fileDataStore = new InFileDataStore(configuration, getFilePathProvider(dirPath));

            var bcrypt = new BCryptPasswordEncoder();
            String passwordHash = bcrypt.encode(password);

            var admin = new AdminUser();
            var usersCollection = fileDataStore.getCollection(users);
            admin.setId(usersCollection.getLastID() + 1);
            admin.setEmail(email);
            admin.setPasswordHash(passwordHash);

            fileDataStore.getCollection(users).insert(admin);
        } else {
            System.out.println("Invalid command");
        }
    }

    private static Function<DataStoreCollectionDescriptor<?>, String> getFilePathProvider(String dirPath) {
        return descriptor -> Path.of(dirPath, descriptor.getName() + ".bin").toString();
    }
}