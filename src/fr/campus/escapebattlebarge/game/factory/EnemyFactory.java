package fr.campus.escapebattlebarge.game.factory;

import fr.campus.escapebattlebarge.domain.character.Enemy;
import fr.campus.escapebattlebarge.game.board.TileType;

/*
 * Cette classe crée les ennemis à partir du type de case du plateau.
 * Elle est utilisée par le contrôleur quand le joueur arrive sur une case hostile.
 * Entrées: TileType. Sorties: instance Enemy prête pour le combat.
 */
public class EnemyFactory {
    // Retourne l'ennemi correspondant au type de case rencontré.
    public Enemy createForTileType(TileType tileType) {
        return switch (tileType) {
            case ENEMY_ORK -> new Enemy("Ork", 16, 3, 6, false);
            case ENEMY_SORCERER -> new Enemy("Sorcier", 20, 4, 7, false);
            case ENEMY_SQUIG -> new Enemy("Squig", 24, 5, 8, false);
            case ENEMY_WARBOSS -> createBoss();
            default -> throw new IllegalArgumentException("Type de case ennemi invalide: " + tileType);
        };
    }

    // Crée le boss final (combat d'extraction).
    public Enemy createBoss() {
        return new Enemy("Warboss", 45, 5, 10, true);
    }
}