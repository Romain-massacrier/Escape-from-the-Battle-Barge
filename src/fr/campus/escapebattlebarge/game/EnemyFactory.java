package fr.campus.escapebattlebarge.game;

import fr.campus.escapebattlebarge.domain.Enemy;

public class EnemyFactory {
    public Enemy createForZone(Zone zone) {
        return switch (zone) {
            case CASERNE -> new Enemy("Ork pillard", 14, 2, 5, false);
            case COURSIVES -> new Enemy("Gretchin vicieux", 12, 2, 4, false);
            case SANCTUAIRE -> new Enemy("Fanatique corrompu", 16, 3, 5, false);
            case ARMEMENTS -> new Enemy("Nob Ork", 20, 3, 7, false);
            case NOYAUX -> new Enemy("Mutant irradié", 18, 3, 6, false);
            case EXTRACTION -> new Enemy("Traqueur de pont", 22, 4, 8, false);
        };
    }

    public Enemy createBoss() {
        return new Enemy("Boss Ork du point d’extraction", 45, 5, 10, true);
    }
}