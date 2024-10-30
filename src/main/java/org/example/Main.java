package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.splitter.FileSplitter;
import org.springframework.integration.support.json.JsonObjectMapperProvider;

import java.io.File;
import java.io.IOException;

@EnableIntegration
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    IntegrationFlow flow() {
        return IntegrationFlow.from(Files.inboundAdapter(new File("input")))
                .split(new FileSplitter(true, true, true))
                .handle(m -> {
                    if (m.getHeaders().containsKey(FileHeaders.MARKER)) {
                        try {
                            FileSplitter.FileMarker fileMarker = JsonObjectMapperProvider.newInstance()
                                    .fromJson(m.getPayload(), FileSplitter.FileMarker.class);
                            System.out.println(fileMarker);
                        } catch (IOException e) {
                            // Unrecognized character escape '$' (code 36) at...
                            System.out.println(e.getMessage());
                        }
                    }
                })
                .get();
    }
}