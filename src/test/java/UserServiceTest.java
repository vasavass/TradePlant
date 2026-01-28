import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



class UserServiceTest {
    private UserService userService = new UserService();
    @Test
    void shouldThrowExceptionWhenUsernameIsEmpty(){
        User usersa = new User("" , "100");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(usersa);

        });

    }
}
