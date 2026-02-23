package tradeplant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@Tag(name = "Trade operations ", description = "Methods for buying , selling and checking users`s balance")
@RestController
public class TradeController {


    private final TradePlant tradePlant;


    public TradeController(TradePlant tradePlant) {
        this.tradePlant = tradePlant;
    }

    @Operation(summary = "Buy coin ", description = "Cols to Binance for actual price an the moment and windows money from user ")
    @PostMapping("/api/buy")
    public ResponseEntity<String> buyStock(@RequestBody TradeRequest tradeRequest) {

        try {
            tradePlant.buyStock(tradeRequest.getUser(), tradeRequest.getStock(), tradeRequest.getQty());



            String successMessage = "SUCCESS: " + tradeRequest.getUser() + " bought " + tradeRequest.getQty() + " of " + tradeRequest.getStock();
            return ResponseEntity.ok(successMessage);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }


    }

    @GetMapping("/api/user")
    public ResponseEntity<?> getUser(@RequestParam String name) {
        User user = tradePlant.getUser(name);
        if (user == null) {
            return ResponseEntity.status(404).body("User " + name + " not found");
        }
        return ResponseEntity.ok(user);
    }
}