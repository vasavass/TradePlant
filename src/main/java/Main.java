import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Data
@AllArgsConstructor
class Stock {
    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;
    private StockType type;


}

class User{
        private String userName;
        private BigDecimal cashBalance;
        private Map<String , Integer> portfolio = new HashMap<>();
        public User(String userName, String cashBalance ){
            this.userName = userName;
            this.cashBalance = new BigDecimal(cashBalance);
        }

        public String getUserName() {
            return userName;
        }

        public BigDecimal getCashBalance() {
            return cashBalance;
        }
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.cashBalance = this.cashBalance.add(amount);
        } else  {
            System.out.println("Amount is negative");
        }
    }
    public void withdraw(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException ("Cannot withdraw negative amount");
        }


        if (this.cashBalance.compareTo(amount) >= 0) {
            this.cashBalance =  this.cashBalance.subtract(amount);
            System.out.println("Withdrawd " + amount + "$");

        } else {
            throw new  InsufficientFundsException ("Not enough balance") ;
        }
    }
       public void addPortfolio(String symbol, int quantity){
            int curentQuantity = portfolio.getOrDefault(symbol, 0);
            int newQuantity = quantity + curentQuantity;
            portfolio.put(symbol, newQuantity);
            System.out.println ("Portfolio added to the stock");

       }

    public boolean removePortfolio(String symbol , int quantity){
        int currentQuantity = portfolio.getOrDefault(symbol, 0);

        if (currentQuantity == 0) {
            System.out.println("Action " + symbol + " Not in portfolio.");
            return false;
        }
        if (currentQuantity < quantity) {
            System.out.println("You want to sale " + quantity + ", but you have ony " + currentQuantity + " actions " + symbol);
            return false;
        }
        int newQuantity = currentQuantity - quantity;
        if (newQuantity > 0) {
            portfolio.put(symbol, newQuantity);
        } else {
            portfolio.remove(symbol);
        }
        System.out.println("Removed from portfolio " + quantity + " actions " + symbol);
        return true;
    }
       public Map<String , Integer> getPortfolio(){
            return portfolio;
       }

}
        class  InsufficientFundsException  extends RuntimeException{
    public InsufficientFundsException (String message){
        super(message);
    }
        }

    class TradePlant{
        private Map<String , Stock> data = new HashMap<>();
//        private Map<String , User> reguser = new HashMap<>();
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
               throw new  IllegalArgumentException ("User " + username + " not found")  ;
            }
            if (quantity <= 0) {
                throw new  IllegalArgumentException ("Quantity is negative");
            }
            if (stock == null) {
                throw new  IllegalArgumentException ("Stock is null");
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
                throw new  IllegalArgumentException ("User " + username + " not found")  ;
            }
            if (quantity <= 0) {
                throw new  IllegalArgumentException ("Quantity is negative");
            }
            if (stock == null) {
                throw new  IllegalArgumentException ("Stock is null");
            }
            if (user.removePortfolio(symbol, quantity)) {
                BigDecimal revenue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
                user.deposit(revenue);
                System.out.println("Success: User " + username + " sold " + quantity +
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
                throw new  IllegalArgumentException ("User " + username + " not found")  ;
            }
            BigDecimal totalValue = user.getCashBalance();
            Map<String , Integer> portfolio = user.getPortfolio();
            for(Map.Entry<String , Integer> entry : portfolio.entrySet()){
                String symbol = entry.getKey();
                int quantity = entry.getValue();
                Stock stock = data.get(symbol);
                if (stock == null) {
                    throw new  IllegalArgumentException ("Stock is null");
                }
                if (stock != null) {
                    BigDecimal stockPrice = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
                    totalValue = totalValue.add(stockPrice);
                }
            }
            return totalValue;
        }
}
class UserDAO {
    public void saveUser (User user) {
        String sql = "INSERT INTO users (username, balance) VALUES (?, ?)";
        try(Connection cnn = DatabaseConnection.getConnection(); PreparedStatement ps = cnn.prepareStatement(sql) ) {
            ps.setString(1, user.getUserName());
            ps.setBigDecimal(2, user.getCashBalance());
            ps.executeUpdate();
            System.out.println("SUCCESS: "  + " saved user " );
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());

        }

    }
    public User getUser (String username ) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection cn = DatabaseConnection.getConnection();  PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal balance = rs.getBigDecimal("balance");
                User foundUser = new User(username , balance.toString());
                loadPortfolio(cn, foundUser);
                return foundUser;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    private void savePortfolio(Connection cm , User user ) throws SQLException {
       String sql = "DELETE FROM user_stocks WHERE username = ?";
       try (PreparedStatement ps = cm.prepareStatement(sql)) {
            ps.setString(1, user.getUserName());
            ps.executeUpdate();
       }
       String Sql = "INSERT INTO user_stocks (username, symbol , quantity ) VALUES (?, ?, ?)";
       try (PreparedStatement ps = cm.prepareStatement(Sql)) {
           for (Map.Entry<String, Integer> entry : user.getPortfolio().entrySet()) {
               String symbol = entry.getKey();
               int quantity = entry.getValue();
               ps.setString(1, user.getUserName());
               ps.setString(2, symbol);
               ps.setInt(3, quantity);
               ps.executeUpdate();
           }
       }
    }
    public void updateUser (User user) {
        String sql = "UPDATE users SET balance = ? WHERE username = ?";
        try (Connection cna = DatabaseConnection.getConnection(); PreparedStatement pss = cna.prepareStatement(sql)) {
            savePortfolio(cna, user);
            pss.setBigDecimal(1, user.getCashBalance());
            pss.setString(2, user.getUserName());
            pss.executeUpdate();
            System.out.println("SUCCESS: " + user.getUserName() + " updated");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteUser (User user) {
        String sql = "DELETE FROM users Where username = ? ";
        try (Connection cb = DatabaseConnection.getConnection(); PreparedStatement ps = cb.prepareStatement(sql)) {
            ps.setString(1, user.getUserName());
            ps.executeUpdate();
            System.out.println("SUCCESS: " + user.getUserName() + " deleted");
        }
        catch (SQLException e ){
            throw new RuntimeException(e);
        }
    }
    private void loadPortfolio(Connection cn , User user) throws SQLException {
        String sql = "SELECT symbol , quantity FROM user_stocks WHERE USERNAME = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, user.getUserName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String symbol = rs.getString("symbol");
                int quantity = rs.getInt("quantity");
                user.getPortfolio().put(symbol, quantity);
            }
        }
    }

}

class UserService{
    private UserDAO userDAO;
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    public void registerUser (User user) {
        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            throw new IllegalArgumentException ("Username is null or empty");
        }
        userDAO.saveUser (user);
    }
    public User getUser(String username){
        return userDAO.getUser(username);
    }
}


public class Main {
    public static void main(String[] args) {
//         TradePlant platform = new TradePlant();
//        Scanner sc = new Scanner(System.in);
//        Stock appl = new Stock("AAPL", "Apple Inc.", "150.00", StockType.COMMON);
//        Stock googl = new Stock("GOOGL", "Alphabet Inc.", "2500.00", StockType.PREFERRED);
//        Stock tsla = new Stock("TSLA", "Tesla Inc.", "900.50", StockType.COMMON);
//        platform.addStock(appl);
//        platform.addStock(googl);
//        platform.addStock(tsla);
//        User alice = new User("Alice", "10000.00");
//        platform.registerUser(alice);
//
//        while (true) {
//            System.out.println("===MENU===");
//            System.out.println("1. Balance");
//            System.out.println("2. Buy");
//            System.out.println("3. Sell");
//            System.out.println("4. Exit");
//            System.out.print("Enter your choice: ");
//            int choice;
//            try {
//                String input = sc.nextLine();
//                choice = Integer.parseInt(input);
//            } catch (NumberFormatException e) {
//                System.out.println("Error: Please enter a valid number (1-4).");
//                continue;
//            }
//            try{
//            switch (choice) {
//                case 1:
//                    BigDecimal value = platform.calculatePortfolio("Alice");
//                    System.out.println("Balance: " + value);
//                    break;
//                case 2:
//                    System.out.println("==Buy==");
//                    System.out.print("Enter name of stock  (e.g. AAPL(Apple Inc): ");
//                    String buyStock = sc.nextLine();
//                    System.out.println("Enter Quantity");
//                    String buyQtyStr = sc.nextLine();
//                    int quantity = Integer.parseInt(buyQtyStr);
//                    platform.buyStock("Alice", buyStock, quantity);
//                    break;
//                case 3:
//                    System.out.println("==Sell==");
//                    System.out.print("Enter name of stock  (e.g. AAPL(Apple Inc): ");
//                    String sellStock = sc.nextLine();
//                    System.out.println("Enter Quantity");
//                    String sellQtyStr = sc.nextLine();
//                    int quantityToSell = Integer.parseInt(sellQtyStr);
//                    platform.sellStock("Alice", sellStock, quantityToSell);
//                    break;
//                case 4:
//                    System.out.println("Exiting system. Goodbye.");
//                    System.exit(0);
//                    break;
//                default:
//                    System.out.println("Unknown command. Try 1-4.");
//            }
//
//            }catch (Exception e) {
//                System.out.println("OPERATION FAILED: " + e.getMessage());
//            }
//        }
//        try(Connection cn = DatabaseConnection.getConnection()) {
//            if (cn != null) {
//                System.out.println("Opening connection to database...");
//                cn.close();
//            }
//        }
//        catch (SQLException e) {
//            System.out.println("SQLException: " + e.getMessage());
//            System.out.println("SQLState: " + e.getSQLState());
//        }
        UserDAO dao1 = new UserDAO();
        TradePlant platform = new TradePlant(dao1);
        Stock apple = new Stock("AAPL", "Apple",new BigDecimal("150.00"), StockType.COMMON);
        platform.addStock(apple);
        User  d = new User("Alice", "100000.00");
//        platform.registerUser(d);
        System.out.println("Buying Test ");
//       platform.buyStock("Alice" , "AAPL" , 2 );
        UserService userService = new UserService(dao1);

        userService.registerUser(d);
        System.out.println("Alice Balance in DB: " + d.getCashBalance());
        System.out.println("Alice Portfolio: " + d.getPortfolio());
        User testUser = new User("Bob", "500.00");
        UserDAO dao = new UserDAO();
        dao.deleteUser(testUser);

        dao.saveUser(testUser);

        User loadedUser = dao.getUser("Bob");
        loadedUser.deposit(new BigDecimal("100.00"));
        dao.updateUser(loadedUser);
        dao.deleteUser(loadedUser);
        User ghost = dao.getUser("Bob");
        if (ghost == null) {
            System.out.println("User "  + " gone");
        }
        else {
            System.out.println("User " + ghost.getUserName() + " exists");

        }
//        if (loadedUser != null) {
//            System.out.println("Loaded: " + loadedUser.getUserName());
//            System.out.println("Balance: " + loadedUser.getCashBalance());
//        }
//        else {
//            System.out.println("User not found");
//        }


    }
}
