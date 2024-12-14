package gameClasses;

import java.io.Serializable;

/**
 * Представляет клетку на игровой карте. Клетка может быть занята игроком, полита, на ней может быть построен дом, и для захвата требуется определенное количество юнитов.
 * Реализует интерфейс TileI и сериализуем для сохранения/загрузки состояний игры.
 */
public class Tile implements TileI, Serializable {
    /**Количество требуемых для захвата юнитов*/
    private int requiredUnits;
    /**Флаг занята ли клетка*/
    private boolean occupied;
    /**Флаг полита ли клетка*/
    private boolean watered = false;
    /**Флаг есть ли на клетке дом*/
    private boolean housed = false;
    /**Владелец клетки*/
    private Player owner = null;

    /**
     * Создает новый объект Tile.
     * @param requiredUnits Количество юнитов, необходимых для захвата этой клетки.
     */
    public Tile(int requiredUnits) {
        this.requiredUnits = requiredUnits;
        this.occupied = false;
    }

    /**
     * Устанавливает клетку как занятую указанным игроком.
     * @param player Игрок, который занимает клетку.
     */
    public void setOccupied(Player player) {
        this.occupied = true;
        this.owner = player;
    }

    /**
     * Проверяет, занята ли клетка.
     * @return True, если клетка занята, false - иначе.
     */
    public boolean isOccupied(){
        return occupied;
    }

    /**
     * Возвращает количество юнитов, необходимых для захвата клетки.
     * @return Количество необходимых юнитов.
     */
    public int getRequiredUnits(){
        return requiredUnits;
    }

    /**
     * Устанавливает количество юнитов, необходимых для захвата клетки.
     * @param requiredUnits Новое количество необходимых юнитов.
     */
    public void setRequiredUnits(int requiredUnits){
        this.requiredUnits=requiredUnits;
    }

    /**
     * Возвращает игрока, владеющего клеткой.
     * @return Игрок, владеющий клеткой, или null, если клетка не занята.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Проверяет, полита ли клетка.
     * @return True, если тайл полита, false - иначе.
     */
    public boolean isWatered() {
        return watered;
    }

    /**
     * Устанавливает, полита ли клетка.
     * @param watered True, если клетку нужно полить, false - иначе.
     */
    public void setWatered(boolean watered) {
        this.watered = watered;
    }

    /**
     * Проверяет, есть ли на клетке дом.
     * @return True, если на клетке есть дом, false - иначе.
     */
    public boolean isHoused() {
        return housed;
    }

    /**
     * Устанавливает, есть ли на клетке дом.
     * @param housed True, если на клетке нужно построить дом, false - иначе.
     */
    public void setHoused(boolean housed) {
        this.housed = housed;
    }
}