package gameClasses;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Random;

/**
 * Представляет игровую карту в игре.
 * Карта представляет собой двумерный массив клеток, каждая из которых имеет определенное количество необходимых юнитов для захвата.
 * Класс также отслеживает уровень риса на каждой клетке и состояния клеток (захвачена, полита, построен дом и т.д.)
 * Реализует интерфейс GameMapI и сериализуем для сохранения/загрузки игры.
 */
public class GameMap implements GameMapI, Serializable {
    /**
     * Логгер для записи сообщений в лог-файл.
     */
    private static final Logger logger = LogManager.getLogger(GameMap.class);
    /**Массив клеток игрового поля*/
    private Tile[][] map;
    /**Массив уровней риса*/
    private double[][] riceLevels;

    /**
     * Создает игровую карту заданного размера.
     * Инициализирует клетки карты и уровни риса на каждой клетке.
     * Устанавливает случайное количество необходимых юнитов для захвата каждой клетки.
     * @param size Размер карты (квадратная карта size x size).
     */
    public GameMap(int size) {
        logger.info("Creating game map with size: " + size);

        map = new Tile[size][size];
        riceLevels = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                riceLevels[i][j] = 1;
            }
        }
        logger.debug("Initialized rice levels.");

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j] = new Tile(0);
            }
        }
        logger.debug("Initialized map tiles.");

        Random random = new Random();
        int currentValue = 0;

        for (int i = 0; i < size; i++) {
            int p = currentValue - 1;
            for (int j = 0; j < size - currentValue; j++) {
                int requiredUnits = currentValue + j + random.nextInt(3);
                map[i][j].setRequiredUnits(requiredUnits);
                p += 1;
            }
            for (int k = size - currentValue; k < size; k++) {
                p -= 1;
                int requiredUnits = p + random.nextInt(3);
                map[i][k].setRequiredUnits(requiredUnits);
            }
            currentValue += 1;
        }
        logger.info("Game map created successfully.");
    }

    /**
     * Увеличивает количество риса на клетках, контролируемых указанным игроком.
     * Рис растет на 1 единицу в день, если клетка не полита, и на 2 единицы, если полита.
     * Максимальный уровень риса на клетке ограничен 3 единицами.
     * @param player Игрок, контролирующий клетки.
     */
    public void growRice(Player player) {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (isControlled(i, j, player)) {
                    double currentRice = getRiceAt(i, j);
                    if (isWatered(i, j)) {
                        setRiceAt(i, j, Math.min(currentRice + 2, 3));
                    } else {
                        setRiceAt(i, j, Math.min(currentRice + 1, 2));
                    }
                }
            }
        }
    }

    /**
     * Попытка захвата клетки игроком.
     * Если клетка не занята и у игрока достаточно юнитов, клетка захватывается, и количество юнитов игрока уменьшается.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param player Игрок, пытающийся захватить клетку.
     * @return True, если захват успешен, false - в противном случае.
     */
    public boolean claimTile(int x, int y, Player player) {
        if (map[x][y].isOccupied()) {
            return false;
        }
        if (player.getUnits() >= map[x][y].getRequiredUnits()) {
            map[x][y].setOccupied(player);
            player.setUnits(player.getUnits() - map[x][y].getRequiredUnits());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Возвращает строку, описывающую состояние клетки.
     * Строка содержит информацию о том, занята ли клетка, кто его владелец, есть ли на ней дом и полит ли рис.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param player Игрок, относительно которого определяется состояние клетки.
     * @return Строка, описывающая состояние клетки. Возможные значения: EMPTY, RICE1, RICE2, RICEWATER1, RICEWATER2, HOUSE1, HOUSE2, HOUSEWATER1, HOUSEWATER2.
     */
    public String getStateString(int x, int y, Player player) {
        Tile tile = map[x][y];
        if (tile.isOccupied()){
            if (tile.getOwner() == player){
                if (tile.isHoused()){
                    if (tile.isWatered()){
                        return "HOUSEWATER1";
                    } else {
                        return "HOUSE1";
                    }
                } else {
                    if (tile.isWatered()){
                        return "RICEWATER1";
                    } else {
                        return"RICE1";
                    }
                }
            } else {
                if (tile.isHoused()){
                    if (tile.isWatered()){
                        return "HOUSEWATER2";
                    } else {
                        return "HOUSE2";
                    }
                } else {
                    if (tile.isWatered()){
                        return "RICEWATER2";
                    } else {
                        return "RICE2";
                    }
                }
            }
        } else {
            return "EMPTY";
        }
    }

    /**
     * Устанавливает начальную клетку для игрока.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param player Игрок.
     */
    public void setStartTile(int x, int y, Player player) {
        map[x][y].setOccupied(player);
    }

    /**
     * Проверяет, находятся ли координаты внутри границ карты.
     * @param x Координата x.
     * @param y Координата y.
     * @return True, если координаты внутри границ, false - иначе.
     */
    public boolean isWithinBounds(int x, int y){
        return x >= 0 && x < map.length && y >= 0 && y < map[0].length;
    }

    /**
     * Проверяет, контролирует ли указанный игрок клетку.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param player Игрок.
     * @return True, если игрок контролирует клетку, false - иначе.
     */
    public boolean isControlled(int x, int y, Player player){
        return map[x][y].getOwner() == player;
    }

    /**
     * Поливает клетку.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     */
    public void waterTile(int x, int y){
        map[x][y].setWatered(true);
    }

    /**
     * Строит дом на клетке.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     */
    public void houseTile(int x, int y){
        map[x][y].setHoused(true);
    }

    /**
     * Возвращает ширину карты.
     * @return Ширина карты.
     */
    public int getWidth() {
        return map.length;
    }

    /**
     * Возвращает высоту карты.
     * @return Высота карты.
     */
    public int getHeight() {
        return map[0].length;
    }

    /**
     * Проверяет, полита ли клетка.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @return True, если клетки полита, false - иначе.
     */
    public boolean isWatered(int x, int y){
        return map[x][y].isWatered();
    }

    /**
     * Проверяет, есть ли дом на клетке.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @return True, если на клетке есть дом, false - иначе.
     */
    public boolean isHoused(int x, int y){
        return map[x][y].isHoused();
    }

    /**
     * Возвращает количество риса на клетке.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @return Количество риса на клетке.
     */
    public double getRiceAt(int x, int y){
        return riceLevels[x][y];
    }

    /**
     * Устанавливает количество риса на клетке.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param amount Количество риса.
     */
    public void setRiceAt(int x, int y, double amount){
        riceLevels[x][y] = amount;
    }

    /**
     * Возвращает клетку по координатам.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @return Клетка.
     */
    public Tile getTile(int x, int y){
        return map[x][y];
    }
}
