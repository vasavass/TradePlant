package tradeplant;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExternalStockDTO {
    private String symbol;
  private BigDecimal price;
}
