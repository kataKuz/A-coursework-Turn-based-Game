package gameClasses;

import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Map;

/**
 * Главный класс игры, реализует графический интерфейс и игровую логику.
 */
public interface GameI {
    /**
     * Отображает главное меню игры.
     * Создает окно меню с кнопками "Начать новую игру", "Загрузить игру" и "Выход".
     * Обрабатывает события нажатия кнопок, запускает новую игру, загрузку игры или завершает работу приложения.
     */
    void menu();
    /**
     * Создает и отображает игровой интерфейс.
     * Инициализирует игровое окно и его элементы: карту, панели игроков, панель действий и панель событий.
     * Добавляет обработчик закрытия окна для сохранения игры.
     * @param mapSize Размер игровой карты.
     */
    void interfaceBuilder(int mapSize);
    /**
     * Создает кнопку для клетки на игровой карте.
     * Устанавливает иконку в зависимости от состояния клетки и добавляет обработчик событий.
     * @param i Координата X клетки.
     * @param j Координата Y клетки.
     * @param player Игрок, для которого отображается состояние клетки.
     * @return Кнопка, представляющая клетку на карте.
     */
    JButton createTileButton(int i, int j, Player player);
    /**
     * Загружает изображения для клеток игровой карты из ресурсов приложения.
     * Изображения хранятся в папке "/images/" и имеют соответствующие названия.
     * Обрабатывает исключения IOException при ошибке загрузки изображений.
     */
    void loadTileImages();
    /**
     * Добавляет характеристики игрока на указанную панель.
     * Создает массив JLabel с характеристиками игрока (территории, вода, рис, крестьяне, дома)
     * и добавляет их на панель.
     * @param panel Панель, на которую добавляются характеристики.
     * @param player Игрок, чьи характеристики отображаются.
     * @return Массив JLabel с характеристиками игрока.
     */
    JLabel[] addPlayerCharacteristics(JPanel panel, Player player);
    /**
     * Обрабатывает событие клика на клетку игровой карты.
     * Устанавливает флаг isTileSelected, запоминает координаты выбранной клетки и активирует кнопки действий.
     * @param i Координата X клетки.
     * @param j Координата Y клетки.
     * @param state Состояние клетки.
     */
    void handleTileClick(int i, int j, String state);
    /**
     * Обрабатывает нажатие кнопки действия.
     * Выполняет действие в зависимости от нажатой кнопки (сбор воды, захват территории, полив риса, строительство дома).
     * Обновляет характеристики игроков и карту после выполнения действия.
     * @param e Событие нажатия кнопки.
     */
    void handleActionClick(ActionEvent e);
    /**
     * Создает кнопки действий игрока и добавляет их на указанную панель.
     * Кнопки изначально отключены и активируются при выборе клетки.
     * @param panel Панель, на которую добавляются кнопки действий.
     */
    void createActionButtons(JPanel panel);
    /**
     * Обновляет отображение игровой карты в соответствии с текущим состоянием клетки.
     * Перерисовывает все клетки карты, обновляя иконки.
     * Обрабатывает возможные ошибки при обновлении клетки.
     */
    void updateMap();
    /**
     * Обновляет отображение характеристик игроков на панели.
     * Перерисовывает значения территорий, воды, риса, крестьян и домов для обоих игроков.
     */
    void updatePlayerCharacteristics();
    /**
     * Выводит сообщение в консольное окно игры - окно событий.
     * Использует SwingUtilities.invokeLater для безопасного обновления UI из другого потока.
     * @param message Сообщение для вывода.
     */
    void printToConsole(String message);
    /**
     * Отображает диалоговое окно с правилами игры.
     * Создает диалоговое окно, содержащее текст с правилами игры, и делает его видимым.
     */
    void showRules();
    /**
     * Отображает диалоговое окно с результатом игры.
     * Показывает сообщение о победе или поражении и кнопки "Закрыть игру" и "Показать графики".
     * @param playerWon Флаг, указывающий на победу игрока (true) или поражение (false).
     */
    void showEndGameWindow(boolean playerWon);
    /**
     * Выполняет ход игрока (или AI).
     * Если это ход AI, то AI принимает решение о действии (захват территории, другие действия или сбор воды) в зависимости от ресурсов и ситуации на карте.
     * Результат хода выводится в консольное окно событий.
     * @param player Игрок, чей ход выполняется.
     */
    void playerTurn(Player player);
    /**
     * Завершает игровой день.
     * Увеличивает количество риса на карте, собирается рис, крестьяне потребляют его, дома производят новых крестьян.
     * Проверяет условие окончания игры и отображает окно с результатом, если игра закончена.
     */
    void endOfDay();
    /**
     * Проверяет, завершена ли игра.
     * Игра заканчивается, если у одного из игроков закончились крестьяне и дома, или если один из игроков захватил 50% и более клеток.
     * @return True, если игра завершена, false - в противном случае.
     */
    boolean isGameOver();
    /**
     * Сохраняет текущее состояние игры в файл.
     * Сериализует объекты Player, GameMap и gameDay в файл.
     * @param filename Имя файла для сохранения.
     * @throws IOException Если возникает ошибка ввода-вывода.
     */
    void saveGame(String filename) throws IOException;
    /**
     * Загружает состояние игры из файла.
     * Десериализует объекты Player, GameMap и gameDay из файла.
     * @param filename Имя файла для загрузки.
     * @throws IOException Если возникает ошибка ввода-вывода.
     * @throws ClassNotFoundException Если класс объекта не найден.
     */
    void loadGame(String filename) throws IOException, ClassNotFoundException;
    /**
     * Отображает диалоговое окно с графиками изменения ресурсов игроков за время игры.
     * Использует библиотеку JFreeChart для построения графиков.
     */
    void showGraphs();
    /**
     * Обрабатывает данные о ресурсах игрока и формирует словарь для построения графиков.
     * Преобразует список ресурсов игрока в словарь, где ключи - названия ресурсов, а значения - массивы значений ресурсов по дням.
     * @param player Игрок, данные о ресурсах которого обрабатываются.
     * @return Словарь данных о ресурсах игрока.
     */
    Map<String, double[]> processPlayerResources(Player player);
    /**
     * Добавляет данные о ресурсах игрока в набор данных для построения графика.
     * Создает отдельный ряд данных для каждого ресурса игрока.
     * @param dataset Набор данных для построения графика.
     * @param resources Словарь данных о ресурсах игрока.
     * @param playerName Имя игрока.
     */
    void addResourcesToDataset(XYSeriesCollection dataset, Map<String, double[]> resources, String playerName);
}
