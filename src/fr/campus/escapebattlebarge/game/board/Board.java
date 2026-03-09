package fr.campus.escapebattlebarge.game.board;

import java.util.*;

/*
 * Cette classe représente le plateau 8x8 (64 cases) de la partie.
 * Elle est utilisée pour savoir ce qu'il y a sur une case (ennemi, trésor, vide).
 * Entrée: index de case demandé. Sortie: Tile correspondante.
 */
/** Plateau de jeu 8x8 contenant les cases et leurs types. */
public class Board {
    private final Map<Integer, Tile> tiles = new HashMap<>();

    // Construit un plateau vide puis applique la répartition ennemis/trésors.
    public Board() {
        BoardLayout.validate();

        for (int i = 1; i <= 64; i++) {
            Zone z = Zone.fromCell(i);
            tiles.put(i, new Tile(i, z, TileType.EMPTY));
        }

        applyEnemyLayout();
        applyTreasureLayout();
    }

    // Place un type de case en vérifiant qu'elle existe bien.
    private void setTypeSafe(int cell, TileType type) {
        Tile t = tiles.get(cell);
        if (t != null) t.setType(type);
    }

    // Pose tous les ennemis selon la configuration centrale du BoardLayout.
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

    // Pose les trésors en laissant la priorité aux cases ennemies.
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

    // Retourne la case demandée, ou null si l'index est hors plateau.
    public Tile getTile(int cell) {
        return tiles.get(cell);
    }
}