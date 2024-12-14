import gameClasses.Game;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Главный класс приложения.
 * Запускает приложение.
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Точка входа в приложение.
     * Инициализирует Log4j, создает экземпляр игровой системы и запускает главное меню.
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        Game game = new Game(10);
        game.menu();
    }
}