package tradeplant;
import lombok.Data;

@Data
public class TradeRequest {
    private String user;
    private String stock;
    private int qty;
}
