package fr.campus.escapebattlebarge.game;

public class Tile {
    private final int index;       // 1..64
    private final Zone zone;
    private TileType type;

    public Tile(int index, Zone zone, TileType type) {
        this.index = index;
        this.zone = zone;
        this.type = type;
    }

    public int getIndex() { return index; }
    public Zone getZone() { return zone; }
    public TileType getType() { return type; }
    public void setType(TileType type) { this.type = type; }
}