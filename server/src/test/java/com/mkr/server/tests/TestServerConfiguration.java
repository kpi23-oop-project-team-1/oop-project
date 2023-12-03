package com.mkr.server.tests;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.DataStoreCollectionDescriptor;
import com.mkr.datastore.inFile.InFileDataStore;
import com.mkr.server.config.DataStoreConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

@TestConfiguration
public class TestServerConfiguration {
    public static final Path dataStorePath = Path.of("test_data");

    @Bean
    @Primary
    public DataStore testDataStore() throws IOException {
        Files.createDirectories(dataStorePath);

        Function<DataStoreCollectionDescriptor<?>, String> filePathMapper =
            descriptor -> dataStorePath.resolve(descriptor.getName() + ".bin").toString();

        return new InFileDataStore(DataStoreConfig.configuration, filePathMapper);
    }
}
