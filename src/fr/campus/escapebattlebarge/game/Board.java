package fr.campus.escapebattlebarge.game;

import java.util.*;

public class Board {
    private final Map<Integer, Tile> tiles = new HashMap<>();
    private final Random rng = new Random();

    public Board() {
        for (int i = 1; i <= 64; i++) {
            Zone z = Zone.fromCell(i);
            tiles.put(i, new Tile(i, z, TileType.NORMAL));
        }
        placeSpecialsPerZone();
    }

    private void placeSpecialsPerZone() {
        for (Zone z : Zone.values()) {
            List<Integer> cells = new ArrayList<>();
            for (int i = z.getStart(); i <= z.getEnd(); i++) cells.add(i);

            // On évite de mettre un event en case de départ de zone si tu veux plus tard.
            // Pour l’instant, on autorise tout.
            Collections.shuffle(cells, rng);

            // 2 monstres + 1 trésor
            setTypeSafe(cells.get(0), TileType.MONSTER);
            setTypeSafe(cells.get(1), TileType.MONSTER);
            setTypeSafe(cells.get(2), TileType.TREASURE);
        }
    }

    private void setTypeSafe(int cell, TileType type) {
        Tile t = tiles.get(cell);
        if (t != null) t.setType(type);
    }

    public Tile getTile(int cell) {
        return tiles.get(cell);
    }
}