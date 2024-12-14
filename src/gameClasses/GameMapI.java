package gameClasses;

/**
 * Представляет игровую карту в игре.
 * Карта представляет собой двумерный массив клеток, каждая из которых имеет определенное количество необходимых юнитов для захвата.
 * Класс также отслеживает уровень риса на каждой клетке и состояния клеток (захвачена, полита, построен дом и т.д.)
 */
public interface GameMapI {
    /**
     * Увеличивает количество риса на клетках, контролируемых указанным игроком.
     * Рис растет на 1 единицу в день, если клетка не полита, и на 2 единицы, если полита.
     * Максимальный уровень риса на клетке ограничен 3 единицами.
     * @param player Игрок, контролирующий клетки.
     */
    void growRice(Player player);
    /**
     * Попытка захвата клетки игроком.
     * Если клетка не занята и у игрока достаточно юнитов, клетка захватывается, и количество юнитов игрока уменьшается.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param player Игрок, пытающийся захватить клетку.
     * @return True, если захват успешен, false - в противном случае.
     */
    boolean claimTile(int x, int y, Player player);
    /**
     * Возвращает строку, описывающую состояние клетки.
     * Строка содержит информацию о том, занята ли клетка, кто его владелец, есть ли на ней дом и полит ли рис.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param player Игрок, относительно которого определяется состояние клетки.
     * @return Строка, описывающая состояние клетки. Возможные значения: EMPTY, RICE1, RICE2, RICEWATER1, RICEWATER2, HOUSE1, HOUSE2, HOUSEWATER1, HOUSEWATER2.
     */
    String getStateString(int x, int y, Player player);
    /**
     * Устанавливает начальную клетку для игрока.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param player Игрок.
     */
    void setStartTile(int x, int y, Player player);
    /**
     * Проверяет, находятся ли координаты внутри границ карты.
     * @param x Координата x.
     * @param y Координата y.
     * @return True, если координаты внутри границ, false - иначе.
     */
    boolean isWithinBounds(int x, int y);
    /**
     * Проверяет, контролирует ли указанный игрок клетку.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param player Игрок.
     * @return True, если игрок контролирует клетку, false - иначе.
     */
    boolean isControlled(int x, int y, Player player);
    /**
     * Поливает клетку.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     */
    void waterTile(int x, int y);
    /**
     * Строит дом на клетке.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     */
    void houseTile(int x, int y);
    /**
     * Возвращает ширину карты.
     * @return Ширина карты.
     */
    int getWidth();
    /**
     * Возвращает высоту карты.
     * @return Высота карты.
     */
    int getHeight();
    /**
     * Проверяет, полита ли клетка.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @return True, если клетки полита, false - иначе.
     */
    boolean isWatered(int x, int y);
    /**
     * Проверяет, есть ли дом на клетке.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @return True, если на клетке есть дом, false - иначе.
     */
    boolean isHoused(int x, int y);
    /**
     * Возвращает количество риса на клетке.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @return Количество риса на клетке.
     */
    double getRiceAt(int x, int y);
    /**
     * Устанавливает количество риса на клетке.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @param amount Количество риса.
     */
    void setRiceAt(int x, int y, double amount);
    /**
     * Возвращает клетку по координатам.
     * @param x Координата x клетки.
     * @param y Координата y клетки.
     * @return Клетка.
     */
    Tile getTile(int x, int y);
}