package tradeplant;

import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.*;


        class  InsufficientFundsException  extends RuntimeException{
    public InsufficientFundsException (String message){
        super(message);
    }
        }




public class Main {
    public static void main(String[] args) {
//         org.vasavass.tradeplant.TradePlant platform = new org.vasavass.tradeplant.TradePlant();
//        Scanner sc = new Scanner(System.in);
//        org.vasavass.tradeplant.Stock appl = new org.vasavass.tradeplant.Stock("AAPL", "Apple Inc.", "150.00", org.vasavass.tradeplant.StockType.COMMON);
//        org.vasavass.tradeplant.Stock googl = new org.vasavass.tradeplant.Stock("GOOGL", "Alphabet Inc.", "2500.00", org.vasavass.tradeplant.StockType.PREFERRED);
//        org.vasavass.tradeplant.Stock tsla = new org.vasavass.tradeplant.Stock("TSLA", "Tesla Inc.", "900.50", org.vasavass.tradeplant.StockType.COMMON);
//        platform.addStock(appl);
//        platform.addStock(googl);
//        platform.addStock(tsla);
//        org.vasavass.tradeplant.User alice = new org.vasavass.tradeplant.User("Alice", "10000.00");
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
//        try(Connection cn = org.vasavass.tradeplant.DatabaseConnection.getConnection()) {
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
        loadedUser.addPortfolio("TSLA", 5);
        loadedUser.addPortfolio("AMZN", 10);
//        dao.deleteUser(loadedUser);
        User ghost = dao.getUser("Bob");
        if (ghost == null) {
            System.out.println("org.vasavass.tradeplant.User "  + " gone");
        }
        else {
            System.out.println("org.vasavass.tradeplant.User " + ghost.getUserName() + " exists");

        }
//        if (loadedUser != null) {
//            System.out.println("Loaded: " + loadedUser.getUserName());
//            System.out.println("Balance: " + loadedUser.getCashBalance());
//        }
//        else {
//            System.out.println("org.vasavass.tradeplant.User not found");
//        }
        System.out.println("\nJSON TEST");
        try {

            ObjectMapper mapper = new ObjectMapper();

            String jsonOutput = mapper.writeValueAsString(loadedUser);

            System.out.println("Java Object: " + loadedUser);
            System.out.println("JSON Result: " + jsonOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            HttpServer server = HttpServer.create(new InetSocketAddress(8084), 0);
            server.createContext("/api/user", exchange -> {
                User realUser = dao1.getUser("Alice");
                String response = "";
                int statusCode = 200;
                if (realUser != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    response = mapper.writeValueAsString(realUser);
                }
                else {
                    response = "{\"statusCode\":" + statusCode + "}";
                    statusCode = 404;
                }
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(statusCode, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
            server.createContext("/api/deposit", exchange -> {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = queryToMap(query);


                String userNameFromUrl = params.get("user");
                String amountFromUrl = params.get("amount");

                System.out.println("DEBUG: Deposit request for " + userNameFromUrl + ", amount: " + amountFromUrl);

                String response = "";
                int statusCode = 200;


                User targetUser = dao1.getUser(userNameFromUrl);

                if (targetUser == null) {
                    response = "org.vasavass.tradeplant.User " + userNameFromUrl + " not found";
                    statusCode = 404;
                } else {
                    try {
                        BigDecimal amountToAdd = new BigDecimal(amountFromUrl);
                        targetUser.deposit(amountToAdd);
                        dao1.updateUser(targetUser);

                        response = "Success! New balance for " + userNameFromUrl + ": " + targetUser.getCashBalance();
                    } catch (Exception e) {
                        response = "Error: Invalid amount";
                        statusCode = 400;
                    }
                }

                exchange.sendResponseHeaders(statusCode, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
            server.createContext("/api/buy", exchange -> {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    Map<String, String> params = queryToMap(query);

                    String userName = params.get("user");
                    String stockSymbol = params.get("stock");
                    int quantity = Integer.parseInt(params.get("qty"));

                    System.out.println("DEBUG: Buy request: " + userName + " wants " + quantity + " of " + stockSymbol);


                    platform.buyStock(userName, stockSymbol, quantity);

                    String response = "SUCCESS: " + userName + " bought " + quantity + " " + stockSymbol;
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                } catch (Exception e) {

                    String errorMsg = "TRANSACTION FAILED: " + e.getMessage();
                    System.out.println(errorMsg);

                    exchange.sendResponseHeaders(400, errorMsg.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(errorMsg.getBytes());
                    os.close();
                }
            });
            server.setExecutor(null);
            server.start();
            System.out.println("Server is running on http://localhost:8082/api/user");
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
    public  static Map<String, String> queryToMap(String query) {

        Map<String, String> result = new HashMap<>();


        if (query == null || query.isEmpty()) {
            return result;
        }


        for (String param : query.split("&")) {

            String[] entry = param.split("=");

            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
