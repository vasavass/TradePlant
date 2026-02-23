package tradeplant;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Entity
@Table(name = "users")
public class User {
    @Id
    private String userName;
    private BigDecimal cashBalance;
    @ElementCollection
    private Map<String , Integer> portfolio = new HashMap<>();
    public User(String userName, String cashBalance ){
        this.userName = userName;
        this.cashBalance = new BigDecimal(cashBalance);
    }
    protected User() {
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
