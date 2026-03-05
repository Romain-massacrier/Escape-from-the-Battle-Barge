package fr.campus.escapebattlebarge.game.board;

/*
 * Cette enum liste les contenus possibles d'une case du plateau.
 * Elle est lue par le contrôleur pour déclencher combat, loot ou rien.
 */
public enum TileType {
    EMPTY,
    ENEMY_ORK,
    ENEMY_SORCERER,
    ENEMY_SQUIG,
    ENEMY_WARBOSS,
    TREASURE_POTION,
    TREASURE_BIG_POTION,
}
