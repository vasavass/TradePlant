package tradeplant;

import java.sql.*;
import java.util.Map;
import java.math.BigDecimal;

public class UserDAO {
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
        try (Connection cn = DatabaseConnection.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

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
