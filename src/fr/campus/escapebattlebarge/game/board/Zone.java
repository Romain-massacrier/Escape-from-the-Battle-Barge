package fr.campus.escapebattlebarge.game.board;

/*
 * Cette enum découpe le plateau en zones thématiques (ambiance + progression).
 * Elle sert surtout à afficher la zone courante du joueur dans le statut.
 * Entrée: numéro de case. Sortie: zone correspondante.
 */
public enum Zone {
    CASERNE(1, 8, "Caserne"),
    COURSIVES(9, 20, "Coursives"),
    SANCTUAIRE(21, 32, "Sanctuaire"),
    ARMEMENTS(33, 44, "Baie d’armement"),
    NOYAUX(45, 56, "Noyau plasma"),
    EXTRACTION(57, 64, "Extraction");

    private final int start;
    private final int end;
    private final String label;

    Zone(int start, int end, String label) {
        this.start = start;
        this.end = end;
        this.label = label;
    }

    // Renvoie la première case de la zone.
    public int getStart() { return start; }
    // Renvoie la dernière case de la zone.
    public int getEnd() { return end; }
    // Renvoie le nom lisible affiché à l'écran.
    public String getLabel() { return label; }

    // Retourne la zone qui contient la case donnée.
    public static Zone fromCell(int cell) {
        for (Zone z : values()) {
            if (cell >= z.start && cell <= z.end) return z;
        }
        // ATTENTION : fallback sur EXTRACTION si valeur hors plage.
        return EXTRACTION;
    }
}