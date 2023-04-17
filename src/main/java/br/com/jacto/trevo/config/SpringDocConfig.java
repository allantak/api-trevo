package br.com.jacto.trevo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Trevo")
                        .description("API que gerencia os pulverizadores de fertilizantes da indústria trevo." +
                                " Algumas funcionalidade presente nessa API, gerenciamento de cliente, produtos, pedidos e gerente." +
                                " Objetivo é divulgar um novo portfólio de produtos para os clientes da Indústria Trevo, bem\n" +
                                "como captar as propostas de interesse nesses produtos.")
                        .version("2.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                        ));

    }
}