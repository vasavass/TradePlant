package tradeplant;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            throw new IllegalArgumentException("Username is null or empty");
        }
        userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findById(username).orElse(null);
    }
}
