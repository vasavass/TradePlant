import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @Test
    void shouldSaveUserWhenDataIsValid(){
        User bob = new User("Bob", "100");
        userService.registerUser(bob);
        Mockito.verify(userDAO, Mockito.times(1)).saveUser(bob);
    }
    @Test
    void shouldReturnUserWhenFound(){
        User expectedUser = new User("Alice" , "500");
        Mockito.when(userDAO.getUser("Alice")).thenReturn(expectedUser);
        User actualUser = userService.getUser("Alice");
        Assertions.assertEquals(expectedUser, actualUser);
    }
}
