
package com.inventario.inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Sistema de Gestión de Inventario")
                        .description("API REST para administrar categorías, productos y movimientos de inventario de una tienda de tecnología.")
                        .version("1.0.0"));
    }
}
