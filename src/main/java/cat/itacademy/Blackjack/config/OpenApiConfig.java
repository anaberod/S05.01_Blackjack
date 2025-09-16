package cat.itacademy.Blackjack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI blackjackOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Blackjack API")
                .description("API reactiva de Blackjack (WebFlux + Mongo + MySQL R2DBC)")
                .version("v1"));
    }
}
