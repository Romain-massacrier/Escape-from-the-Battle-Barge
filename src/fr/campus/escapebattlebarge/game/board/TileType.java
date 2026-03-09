package fr.campus.escapebattlebarge.game.board;

import fr.campus.escapebattlebarge.domain.character.Enemy;
import fr.campus.escapebattlebarge.domain.character.enemy.OrkBoy;
import fr.campus.escapebattlebarge.domain.character.enemy.Sorcerer;
import fr.campus.escapebattlebarge.domain.character.enemy.Squig;
import fr.campus.escapebattlebarge.domain.character.enemy.Warboss;

/** Types de cases du plateau. */
public enum TileType {
    EMPTY,
    ENEMY_ORK,
    ENEMY_SORCERER,
    ENEMY_SQUIG,
    ENEMY_WARBOSS,
    TREASURE_POTION,
    TREASURE_BIG_POTION;

    /** Crée l'ennemi associé à une case de type ennemi. */
    public Enemy createEnemy() {
        return switch (this) {
            case ENEMY_ORK -> new OrkBoy();
            case ENEMY_SORCERER -> new Sorcerer();
            case ENEMY_SQUIG -> new Squig();
            case ENEMY_WARBOSS -> new Warboss();
            default -> throw new IllegalStateException("Ce type de case n'est pas un ennemi: " + this);
        };
    }
}
