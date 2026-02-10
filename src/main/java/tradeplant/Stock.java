package tradeplant;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Stock { // <--- ДОБАВЬ PUBLIC
    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;
    private StockType type;
}