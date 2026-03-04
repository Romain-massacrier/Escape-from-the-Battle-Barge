package fr.campus.escapebattlebarge.game;

import java.util.*;

public class Board {
    private final Map<Integer, Tile> tiles = new HashMap<>();

    public Board() {
        BoardLayout.validate();

        for (int i = 1; i <= 64; i++) {
            Zone z = Zone.fromCell(i);
            tiles.put(i, new Tile(i, z, TileType.EMPTY));
        }

        applyEnemyLayout();
        applyTreasureLayout();
    }

    private void setTypeSafe(int cell, TileType type) {
        Tile t = tiles.get(cell);
        if (t != null) t.setType(type);
    }

    private void applyEnemyLayout() {
        for (int cell : BoardLayout.ORK_CELLS) {
            setTypeSafe(cell, TileType.ENEMY_ORK);
        }
        for (int cell : BoardLayout.SORCERER_CELLS) {
            setTypeSafe(cell, TileType.ENEMY_SORCERER);
        }
        for (int cell : BoardLayout.SQUIG_CELLS) {
            setTypeSafe(cell, TileType.ENEMY_SQUIG);
        }
        for (int cell : BoardLayout.WARBOSS_CELLS) {
            setTypeSafe(cell, TileType.ENEMY_WARBOSS);
        }
    }

    private void applyTreasureLayout() {
        // Priorité ennemi si collision
        Set<Integer> enemyCells = BoardLayout.allEnemyCells();
        for (int cell : BoardLayout.POTION_CELLS) {
            if (!enemyCells.contains(cell)) {
                setTypeSafe(cell, TileType.TREASURE_POTION);
            }
        }
        for (int cell : BoardLayout.BIG_POTION_CELLS) {
            if (!enemyCells.contains(cell)) {
                setTypeSafe(cell, TileType.TREASURE_BIG_POTION);
            }
        }
    }

    public Tile getTile(int cell) {
        return tiles.get(cell);
    }
}