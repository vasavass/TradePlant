import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class TradePlantTest {

    @Mock
    private UserDAO userDAO;
    @InjectMocks
    private TradePlant tradePlant;
    @Test
    void shouldDecreaseBalanceWhenBuying() {
        User alice = new User("Alice", "1000");
        Stock aple = new Stock("AAPL", "APLE" ,new BigDecimal("150"),StockType.COMMON);

        tradePlant.addStock(aple);

        Mockito.when(userDAO.getUser("Alice")).thenReturn(alice);

        tradePlant.buyStock("Alice" , "AAPL", 2);
        BigDecimal expectedBalance = new BigDecimal("700");
        Assertions.assertEquals(expectedBalance, alice.getCashBalance());
        Mockito.verify(userDAO).updateUser(alice);
    }
}
