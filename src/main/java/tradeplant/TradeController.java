package tradeplant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class TradeController {

    // 2. Старый добрый DAO (пока создаем его вручную, скоро исправим)
    private UserDAO userDAO = new UserDAO();

    // 3. Маршрут GET /api/user
    // Спринг сам поймет, что user=... это параметр запроса!
    @GetMapping("/api/user")
    public User getUser(@RequestParam String name) {
        return userDAO.getUser(name);
    }
}