package tradeplant;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class TradePlant {

    private final Map<String, Stock> data = new HashMap<>();
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;


    public TradePlant(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public void registerUser(User user) {
        userRepository.save(user);
    }

    public void addStock(Stock stock) {
        data.put(stock.getSymbol(), stock);
    }

    public void buyStock(String username, String symbol, int quantity) {
        User user = userRepository.findById(username).orElse(null);
        Stock stock = data.get(symbol);

        if (user == null) {
            throw new IllegalArgumentException("User " + username + " not found");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity is negative");
        }
        if (stock == null) {
            throw new IllegalArgumentException("Stock is null");
        }
        try {
            String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol;
            ExternalStockDTO info = restTemplate.getForObject(url, ExternalStockDTO.class);

            BigDecimal cost = info.getPrice().multiply(BigDecimal.valueOf(quantity));

            if (cost.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Cost is negative");
            }

            user.withdraw(cost);
            user.addPortfolio(symbol, quantity);

            System.out.println("SUCCESS: " + username + " bought " + quantity + " " + symbol);

            userRepository.save(user);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error fetching price for " + symbol + " from Binance!");
        }
    }

    public void sellStock(String username, String symbol, int quantity) {
        User user = userRepository.findById(username).orElse(null);
        Stock stock = data.get(symbol);

        if (user == null) {
            throw new IllegalArgumentException("User " + username + " not found");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity is negative");
        }
        if (stock == null) {
            throw new IllegalArgumentException("Stock is null");
        }
        try {

            String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol;
            ExternalStockDTO info = restTemplate.getForObject(url, ExternalStockDTO.class);

            if (user.removePortfolio(symbol, quantity)) {
                BigDecimal revenue = info.getPrice().multiply(BigDecimal.valueOf(quantity));
                user.deposit(revenue);

                System.out.println("SUCCESS: " + username + " sold " + quantity + " " + symbol +
                        " for $" + String.format("%.2f", revenue));

                userRepository.save(user);
            } else {
                throw new IllegalArgumentException("Not enough stocks to sell");
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error fetching price for " + symbol + " from Binance!");
        }
    }

    public BigDecimal calculatePortfolio(String username) {
        User user = userRepository.findById(username).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("User " + username + " not found");
        }

        BigDecimal totalValue = user.getCashBalance();

        for (Map.Entry<String, Integer> entry : user.getPortfolio().entrySet()) {
            String symbol = entry.getKey();

            try {


                int quantity = entry.getValue();
                String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol;
                ExternalStockDTO info = restTemplate.getForObject(url, ExternalStockDTO.class);

                Stock stock = data.get(symbol);
                if (stock == null) {
                    throw new IllegalArgumentException("Stock is null");
                }

                totalValue = totalValue.add(
                        info.getPrice().multiply(BigDecimal.valueOf(quantity))
                );
            }
            catch (Exception e) {
                System.out.println("Cant find the price of : " + symbol);
            }
        }

        return totalValue;
    }

    @PostConstruct

    public void initMarket() {
        System.out.println("Init Market");

        Stock apple = new Stock("AAPL", "Apple Inc.", new BigDecimal("0"), StockType.COMMON);
        Stock google = new Stock("GOOGL", "Alphabet Inc.", new BigDecimal("0"), StockType.COMMON);
        Stock btc = new Stock("BTCUSDT", "Bitcoin", new BigDecimal("0"),StockType.COMMON);
        Stock dogusd = new Stock("DOGEUSDT", "Dogus", new BigDecimal("0"),StockType.COMMON);

        this.addStock(apple);
        this.addStock(google);
        this.addStock(btc);
        this.addStock(dogusd);

        
        if (userRepository.findById("Alice").orElse(null) == null) {
            User alice = new User("Alice", "1000000.00");
            userRepository.save(alice);
            System.out.println("DEBUG: Alice spawned in the Database!");
        }
    }
    public User getUser(String username) {
        return userRepository.findById(username).orElse(null);
    }
}
