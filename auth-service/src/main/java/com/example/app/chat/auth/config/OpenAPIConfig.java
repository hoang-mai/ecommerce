package com.example.app.chat.auth.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI(
            @Value("${openapi.service.serverUrl}") String serverUrl
            ,@Value("${openapi.service.serverName}") String serverName
            ,@Value("${openapi.service.title}") String title
            ,@Value("${openapi.service.description}") String description
            ,@Value("${openapi.service.version}") String version){
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl).description(serverName)))
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
    @Bean
    public GroupedOpenApi groupedOpenApi(
            @Value("${openapi.service.api-docs}") String apiDocs){
        return GroupedOpenApi.builder()
                .group(apiDocs)
                .packagesToScan("com.example.app.chat.user.controller")
                .build();
    }
}
