package br.com.jacto.trevo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return (OpenAPI) new OpenAPI()
                .info(new Info()
                        .title("API Trevo")
                        .description("API que gerencia os pulverizadores de fertilizantes da indústria trevo." +
                                " Algumas funcionalidade presente nessa API, gerenciamento de cliente, produtos e pedidos." +
                                " Objetivo é divulgar um novo portfólio de produtos para os clientes da Indústria Trevo, bem\n" +
                                "como captar as propostas de interesse nesses produtos.")
                        .version("1.0.0"));
    }
}