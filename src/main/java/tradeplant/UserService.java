package tradeplant;

public class UserService {
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
