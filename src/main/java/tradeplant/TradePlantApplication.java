package tradeplant;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "TradePlant Crypto Exchange API",
                version = "1.0",
                description = "Pet-project support real price of coins from Binance."
        )
)
@SpringBootApplication
public class TradePlantApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradePlantApplication.class, args);
    }
}
