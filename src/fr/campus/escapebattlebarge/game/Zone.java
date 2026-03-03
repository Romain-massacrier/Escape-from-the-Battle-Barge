package fr.campus.escapebattlebarge.game;

public enum Zone {
    CASERNE(1, 8, "Caserne"),
    COURSIVES(9, 20, "Coursives de service"),
    SANCTUAIRE(21, 32, "Sanctuaire des reliques"),
    ARMEMENTS(33, 44, "Baie d’armement"),
    NOYAUX(45, 56, "Noyau plasma instable"),
    EXTRACTION(57, 64, "Point d’extraction");

    private final int start;
    private final int end;
    private final String label;

    Zone(int start, int end, String label) {
        this.start = start;
        this.end = end;
        this.label = label;
    }

    public int getStart() { return start; }
    public int getEnd() { return end; }
    public String getLabel() { return label; }

    public static Zone fromCell(int cell) {
        for (Zone z : values()) {
            if (cell >= z.start && cell <= z.end) return z;
        }
        return EXTRACTION;
    }
}