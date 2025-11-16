package merchant_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Merchant Manager API")
                        .version("1.0.0")
                        .description("Merchant CRM and Miles Management System API - Manage merchants, contacts, templates, pointing systems, transactions, and file storage")
                        .contact(new Contact()
                                .name("Merchant Manager Team")
                                .email("support@merchantmanager.com"))
                        .license(new License()
                                .name("API License")
                                .url("https://www.merchantmanager.com/license")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT Bearer token")))
                .tags(Arrays.asList(
                        new Tag().name("Users").description("User management and authentication"),
                        new Tag().name("Merchants").description("Merchant management operations"),
                        new Tag().name("Contacts").description("Contact and merchant-contact relationship management"),
                        new Tag().name("Templates").description("Template management for dynamic forms"),
                        new Tag().name("Dynamic Records").description("Dynamic record operations"),
                        new Tag().name("Menu").description("Menu configuration"),
                        new Tag().name("Pointing System").description("Merchant miles pointing system configuration"),
                        new Tag().name("Merchant Miles").description("Merchant miles tracking and management"),
                        new Tag().name("Dejavoo").description("Dejavoo payment integration and transaction processing"),
                        new Tag().name("File Storage").description("File upload, download, and management")
                ));
    }

    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("All APIs")
                .packagesToScan("merchant_manager.controller", "merchant_manager.auth", "merchant_manager.emailService")
                .pathsToExclude("/error", "/actuator/**")
                .build();
    }

    @Bean
    public GroupedOpenApi merchantApis() {
        return GroupedOpenApi.builder()
                .group("Merchant Management")
                .pathsToMatch("/api/contact-merchant/**", "/api/merchant-tpl/**")
                .build();
    }

    @Bean
    public GroupedOpenApi templateApis() {
        return GroupedOpenApi.builder()
                .group("Template Management")
                .pathsToMatch("/api/template/**", "/api/template-form/**", "/api/template-form-default/**", "/api/template-form-value/**", "/api/template-form-value-default/**")
                .build();
    }

    @Bean
    public GroupedOpenApi milesApis() {
        return GroupedOpenApi.builder()
                .group("Miles & Transactions")
                .pathsToMatch("/api/pointing-system/**", "/api/merchant-miles/**", "/api/dejavoo-credentials/**", "/api/dejavoo-transactions/**")
                .build();
    }

    @Bean
    public GroupedOpenApi fileApis() {
        return GroupedOpenApi.builder()
                .group("File Management")
                .pathsToMatch("/api/files/**")
                .build();
    }
}

