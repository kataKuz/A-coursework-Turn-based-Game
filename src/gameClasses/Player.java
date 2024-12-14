package gameClasses;

import java.io.Serializable;
import java.util.*;

/**
 * Представляет игрока в игре. Игрок обладает ресурсами (рис, вода), юнитами, домами и контролирует клетки на игровой карте.
 * Реализует интерфейс PlayerI и сериализуем для сохранения/загрузки состояний игры.
 */
public class Player implements PlayerActions, Serializable {
    /**Координаты клетки х, у*/
    private int x, y;
    /**Количество риса, воды*/
    private double rice, water;
    /**Количество юнитов*/
    private int units;
    /**Количество домов*/
    private int houses = 0;
    /**Количество захваченных клеток*/
    private int controlledTiles = 1;
    /**Список со словарями, хранящими ресурсы игрока*/
    private List<Map<String, Double>> resources = new ArrayList<>();

    /**
     * Создает новый объект Player.
     * @param x Начальная координата x стартовой клетки игрока.
     * @param y Начальная координата y стартовой клетки игрока.
     * @param rice Начальное количество риса.
     * @param water Начальное количество воды.
     * @param units Начальное количество юнитов.
     * @param gameMap Игровая карта, на которой находится игрок. Используется для установки стартовой клетки.
     */
    public Player(int x, int y, double rice, double water, int units, GameMap gameMap) {
        this.x = x;
        this.y = y;
        gameMap.setStartTile(x, y, this);
        this.rice = rice;
        this.water = water;
        this.units = units;
    }

    /**
     * Добавляет указанное количество воды к запасам игрока.
     * @param amount Количество воды, которое нужно добавить.
     */
    public void collectWater(double amount) {
        water = water + amount;
    }

    /**
     * Позволяет игроку полить рис на указанной клетке.
     * Проверяет, находится ли клетка в пределах карты, контролируется ли она игроком и достаточно ли у игрока воды.
     * Если все условия выполнены, клетка поливается, и количество воды у игрока уменьшается на 5 единиц.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param gameMap Игровая карта.
     * @return Сообщение об успешном поливе или ошибке.
     */
    public String waterRice(int x, int y, GameMap gameMap) {
        if (gameMap.isWithinBounds(x, y) && gameMap.isControlled(x, y, this)) {
            if (water < 5){
                return "Недостаточно воды";
            } else {
                water = water - 5;
                gameMap.waterTile(x, y);
                return "Вы полили рис, теперь он растёт быстрее";
            }
        } else {
            return "Нельзя полить рис в этой клетке.";
        }
    }

    /**
     * Попытка захвата территории игроком.
     * Проверяет, находится ли клетка в пределах карты и может ли быть захвачена игроком.
     * Если захват успешен, количество контролируемых клеток игрока увеличивается.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param gameMap Игровая карта.
     * @return Сообщение об успешном захвате или ошибке.
     */
    public String claimTerritory(int x, int y, GameMap gameMap) {
        if (gameMap.isWithinBounds(x, y) && gameMap.claimTile(x, y, this)) {
            controlledTiles++;
            return "Вы освоили территорию";
        } else {
            return "Не удалось освоить территорию";
        }
    }

    /**
     * Попытка постройки дома игроком на указанной клетке.
     * Проверяет, находится ли клетка в пределах карты, контролируется ли она игроком и есть ли на ней уже дом.
     * Если все условия выполнены и достаточно ресурсов (рис, вода, юниты), дом строится, и ресурсы игрока уменьшаются.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param gameMap Игровая карта.
     * @return Сообщение об успешной постройке дома или ошибке.
     */
    public String buildHouse(int x, int y, GameMap gameMap) {
        if (gameMap.isWithinBounds(x, y) && gameMap.isControlled(x, y, this) && !(gameMap.isHoused(x, y))) {
            if (rice >= 25 && water >= 10 && units >= 1) {
                rice =  rice - 25;
                water = water - 10;
                units = units - 1;
                houses = houses + 1;
                gameMap.houseTile(x, y);
                return "Вы построили дом, теперь у вас будет больше крестьян";
            } else {
                return "Недостаточно ресурсов для строительства дома.";
            }
        } else {
            return "Нельзя поcтроить дом в этой клетке.";
        }
    }

    /**
     * Сбор урожая риса с контролируемых игроком клеток.
     * Игрок собирает весь рис с каждой контролируемой клетки, оставляя 1 единицу риса меньше на клетке.
     * @param gameMap Игровая карта.
     */
    public void collectRice(GameMap gameMap){
        for (int i = 0; i < gameMap.getWidth();i++){
            for (int j = 0; j < gameMap.getHeight(); j++) {
                if (gameMap.isControlled(i, j, this)){
                    rice += gameMap.getRiceAt(i, j);
                    gameMap.setRiceAt(i, j, gameMap.getRiceAt(i, j) - 1);
                }
            }
        }
    }

    /**
     * Возвращает количество юнитов игрока.
     * @return Количество юнитов.
     */
    public int getUnits() {
        return units;
    }

    /**
     * Возвращает количество риса у игрока.
     * @return Количество риса.
     */
    public double getRice() {
        return rice;
    }

    /**
     * Возвращает количество воды у игрока.
     * @return Количество воды.
     */
    public double getWater() {
        return water;
    }

    /**
     * Возвращает количество домов, построенных игроком.
     * @return Количество домов.
     */
    public int getHouses(){
        return houses;
    }

    /**
     * Устанавливает количество юнитов игрока.
     * @param units Новое количество юнитов.
     */
    public void setUnits(int units) {
        this.units = units;
    }

    /**
     * Расходует рис на питание (уменьшает количество риса).
     * Проверяет, что количество риса не становится отрицательным.
     * @param amount Количество единиц потребления риса.
     */
    public void eatRice(int amount){
        rice = Math.max(rice - amount * 3, 0);
    }

    /**
     * Возвращает количество контролируемых игроком клеток.
     * @return Количество контролируемых клеток.
     */
    public int controlledTiles() {
        return controlledTiles;
    }

    /**
     * Сохраняет текущие ресурсы игрока в список ресурсов.
     * Создает новый словарь ресурсов для текущего дня и добавляет его в список ресурсов игрока.
     */
    public void saveResources(){
        Map<String, Double> dayResources = new HashMap<>();
        dayResources.put("вода", water);
        dayResources.put("рис", rice);
        double unitsd = (Integer) units;
        dayResources.put("крестьяне", unitsd);
        double housesd = (Integer) houses;
        dayResources.put("дома", housesd);

        resources.add(dayResources);
    }

    /**
     * Возвращает список ресурсов игрока за все дни игры.
     * @return Список словарей, где каждая карта содержит ресурсы за один день.
     */
    public List<Map<String, Double>> getResources(){
        return resources;
    }

    /**
     * Попытка захвата территории ИИ-игроком в радиусе поиска.
     * ИИ использует поиск в ширину (BFS) для поиска ближайшего незанятого тайла, который можно захватить.
     * @param player Игрок ИИ.
     * @param searchRadius Радиус поиска в клетках.
     * @param gameMap Игровая карта.
     * @return Сообщение об успешном захвате или ошибке.
     */
    public String aiPlayerClaimTerritory(Player player, int searchRadius, GameMap gameMap) {
        int startX = 0;
        int startY = 0;

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startX, startY});

        boolean[][] visited = new boolean[gameMap.getWidth()][gameMap.getHeight()];
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            if (gameMap.isWithinBounds(x, y) && gameMap.claimTile(x, y, this)) {
                player.claimTerritory(x, y, gameMap);
                controlledTiles++;
                return "ИИ освоил ближайшую территорию (" + x + ", " + y + ")";
            }

            int[] dx = {0, 0, 1, -1};
            int[] dy = {1, -1, 0, 0};
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (gameMap.isWithinBounds(nx, ny) && !visited[nx][ny] && dist(nx,ny,startX,startY) <= searchRadius) {
                    queue.offer(new int[]{nx, ny});
                    visited[nx][ny] = true;
                }
            }
        }
        return "ИИ не смог ничего освоить";
    }

    /**
     * Вычисляет манхэттенское расстояние между двумя точками (клетками) на карте.
     * @param x1 Координата x первой точки (клетки).
     * @param y1 Координата y первой точки (клетки).
     * @param x2 Координата x второй точки (клетки).
     * @param y2 Координата y второй точки (клетки).
     * @return Манхэттенское расстояние между двумя точками (клетками).
     */
    private int dist(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Возвращает список координат всех клеток, контролируемых указанным игроком.
     * @param player Игрок.
     * @param gameMap Игровая карта.
     * @return Список координат (массивов из двух элементов: x и y) контролируемых клеток.
     */
    private List<int[]> getControlledTiles(Player player, GameMap gameMap) {
        List<int[]> tiles = new ArrayList<>();
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile.isOccupied() && tile.getOwner() == player) {
                    tiles.add(new int[]{x, y});
                }
            }
        }
        return tiles;
    }

    /**
     * Реализует другие действия ИИ-игрока, если захват территории невозможен.
     * ИИ проверяет наличие ресурсов и выполняет полив риса или строительство дома, если это возможно.
     * Если других действий нет, ИИ собирает воду.
     * @param player Игрок ИИ.
     * @param gameMap Игровая карта.
     * @return Сообщение о действии ИИ.
     */
    public String aiPlayerOtherOptions(Player player, GameMap gameMap) {
        List<int[]> controlledTiles = getControlledTiles(player, gameMap);
        if ((rice < units*3) || (rice < 25)) { //Изменённое условие
            for (int[] coords : controlledTiles) {
                int x = coords[0];
                int y = coords[1];
                Tile tile = gameMap.getTile(x, y);
                if (!tile.isWatered()) {
                    player.waterRice(x, y, gameMap);
                    return "ИИ полил рис в клетке (" + x + ", " + y + ")";
                }
            }
        } else {
            for (int[] coords : controlledTiles) {
                int x = coords[0];
                int y = coords[1];
                Tile tile = gameMap.getTile(x, y);
                if (!tile.isHoused() && (rice >= 25 && water >= 10 && units >= 1)) {
                    player.buildHouse(x, y, gameMap);
                    return "ИИ построил дом в клетке (" + x + ", " + y + ")";
                        }
                    }
                }
        collectWater(15);
        return "ИИ набрал воду, больше ему делать нечего :/";
    }
}