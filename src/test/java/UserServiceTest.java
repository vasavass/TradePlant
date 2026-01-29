import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserDAO userDAO;

    @InjectMocks

    private UserService userService ;
    @Test
    void shouldThrowExceptionWhenUsernameIsEmpty(){
        User usersa = new User("" , "100");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(usersa);

        });

    }
}
