package fr.campus.escapebattlebarge.game.board;

/*
 * Cette classe représente une case du plateau (position, zone, contenu).
 * Elle est utilisée par le moteur pour savoir ce qui se passe quand le joueur arrive dessus.
 * Entrées/sorties: lit/écrit le type de case (ennemi, trésor, vide).
 */
public class Tile {
    private final int index;       // 1..64
    private final Zone zone;
    private TileType type;

    // Crée une case avec son index, sa zone, et son contenu initial.
    public Tile(int index, Zone zone, TileType type) {
        this.index = index;
        this.zone = zone;
        this.type = type;
    }

    // Retourne l'index de case (1..64).
    public int getIndex() { return index; }
    // Retourne la zone de cette case.
    public Zone getZone() { return zone; }
    // Retourne le type actuel de cette case.
    public TileType getType() { return type; }
    // Met à jour le type (ex: trésor consommé -> EMPTY).
    public void setType(TileType type) { this.type = type; }
}