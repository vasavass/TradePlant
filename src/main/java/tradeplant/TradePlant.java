package tradeplant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TradePlant {
    private Map<String , Stock> data = new HashMap<>();
    //        private Map<String , org.vasavass.tradeplant.User> reguser = new HashMap<>();
    private UserDAO userDAO;
    public TradePlant(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public void registerUser (User user){
        userDAO.saveUser(user);
    }
    public void addStock(Stock stock) {
        data.put(stock.getSymbol(), stock);
    }
    public void  buyStock(String username , String symbol, int quantity) {
        User user = userDAO.getUser(username);
        Stock stock = data.get(symbol);



        if (user == null) {
            throw new  IllegalArgumentException ("org.vasavass.tradeplant.User " + username + " not found")  ;
        }
        if (quantity <= 0) {
            throw new  IllegalArgumentException ("Quantity is negative");
        }
        if (stock == null) {
            throw new  IllegalArgumentException ("org.vasavass.tradeplant.Stock is null");
        }


        BigDecimal cost = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));


        if (cost.compareTo(BigDecimal.ZERO) <= 0) {
            throw new  IllegalArgumentException ("Cost is negative");
        }
        user.withdraw(cost);
        user.addPortfolio(symbol, quantity);
        System.out.println("SUCCESS: " + username + " bought " + quantity + " " + symbol);
        userDAO.updateUser(user);

    }
    public void sellStock(String username , String symbol, int quantity) {
        User user = userDAO.getUser(username);
        Stock stock = data.get(symbol);

        if (user == null) {
            throw new  IllegalArgumentException ("org.vasavass.tradeplant.User " + username + " not found")  ;
        }
        if (quantity <= 0) {
            throw new  IllegalArgumentException ("Quantity is negative");
        }
        if (stock == null) {
            throw new  IllegalArgumentException ("org.vasavass.tradeplant.Stock is null");
        }
        if (user.removePortfolio(symbol, quantity)) {
            BigDecimal revenue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
            user.deposit(revenue);
            System.out.println("Success: org.vasavass.tradeplant.User " + username + " sold " + quantity +
                    " stocks " + symbol + " for $" + String.format("%.2f", revenue));
            userDAO.updateUser(user);

        }
        else {
            throw new IllegalArgumentException("Not enough stocks to sell");
        }
    }
    public BigDecimal calculatePortfolio(String username ) {
        User user = userDAO.getUser(username);

        if (user == null) {
            throw new  IllegalArgumentException ("org.vasavass.tradeplant.User " + username + " not found")  ;
        }
        BigDecimal totalValue = user.getCashBalance();
        Map<String , Integer> portfolio = user.getPortfolio();
        for(Map.Entry<String , Integer> entry : portfolio.entrySet()){
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            Stock stock = data.get(symbol);
            if (stock == null) {
                throw new  IllegalArgumentException ("org.vasavass.tradeplant.Stock is null");
            }
            if (stock != null) {
                BigDecimal stockPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
                totalValue = totalValue.add(stockPrice);
            }
        }
        return totalValue;
    }
}
