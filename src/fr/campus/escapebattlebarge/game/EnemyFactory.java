package fr.campus.escapebattlebarge.game;

import fr.campus.escapebattlebarge.domain.Enemy;

public class EnemyFactory {
    public Enemy createForTileType(TileType tileType) {
        return switch (tileType) {
            case ENEMY_ORK -> new Enemy("Ork", 16, 3, 6, false);
            case ENEMY_SORCERER -> new Enemy("Sorcier", 20, 4, 7, false);
            case ENEMY_SQUIG -> new Enemy("Squig", 24, 5, 8, false);
            case ENEMY_WARBOSS -> createBoss();
            default -> throw new IllegalArgumentException("Type de case ennemi invalide: " + tileType);
        };
    }

    public Enemy createBoss() {
        return new Enemy("Warboss", 45, 5, 10, true);
    }
}