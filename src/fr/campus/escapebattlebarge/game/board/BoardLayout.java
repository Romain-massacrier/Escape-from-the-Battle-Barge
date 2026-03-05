package fr.campus.escapebattlebarge.game.board;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/*
 * Cette classe contient la répartition fixe des ennemis et trésors sur le plateau.
 * Elle est utilisée au démarrage d'une partie pour construire un board cohérent.
 * Entrées/sorties: expose des ensembles de cases + méthodes de validation.
 */
public final class BoardLayout {

    // Empêche l'instanciation: c'est une classe utilitaire statique.
    private BoardLayout() {}

    // Source de vérité unique des ennemis
    public static final Set<Integer> ORK_CELLS = immutableSet(3, 6, 9, 12, 15, 18, 21, 24, 27, 30);
    public static final Set<Integer> SORCERER_CELLS = immutableSet(10, 20, 25, 32, 35, 36, 37, 40, 44, 47);
    public static final Set<Integer> SQUIG_CELLS = immutableSet(45, 52, 56, 62);
    public static final Set<Integer> WARBOSS_CELLS = immutableSet(64);

    // Source de vérité unique des trésors
    public static final Set<Integer> POTION_CELLS = immutableSet(7, 13, 31, 33, 39, 43);
    public static final Set<Integer> BIG_POTION_CELLS = immutableSet(28, 41);

    // Construit un Set immuable pour éviter les modifications accidentelles.
    private static Set<Integer> immutableSet(int... values) {
        Set<Integer> set = new LinkedHashSet<>();
        for (int value : values) {
            set.add(value);
        }
        return Collections.unmodifiableSet(set);
    }

    // Vérifie les règles du plateau (plages, quantités, collisions, boss final).
    public static void validate() {
        validateRange(ORK_CELLS, "ORK_CELLS");
        validateRange(SORCERER_CELLS, "SORCERER_CELLS");
        validateRange(SQUIG_CELLS, "SQUIG_CELLS");
        validateRange(WARBOSS_CELLS, "WARBOSS_CELLS");
        validateRange(POTION_CELLS, "POTION_CELLS");
        validateRange(BIG_POTION_CELLS, "BIG_POTION_CELLS");

        int enemyTotal = ORK_CELLS.size() + SORCERER_CELLS.size() + SQUIG_CELLS.size() + WARBOSS_CELLS.size();
        if (enemyTotal != 25) {
            throw new IllegalStateException("Répartition invalide: total ennemis attendu=25, trouvé=" + enemyTotal);
        }

        if (allEnemyCells().size() != 25) {
            throw new IllegalStateException("Répartition invalide: doublons détectés entre cases ennemies");
        }

        if (POTION_CELLS.size() != 6) {
            throw new IllegalStateException("Répartition invalide: potions standards attendues=6, trouvées=" + POTION_CELLS.size());
        }

        if (BIG_POTION_CELLS.size() != 2) {
            throw new IllegalStateException("Répartition invalide: grandes potions attendues=2, trouvées=" + BIG_POTION_CELLS.size());
        }

        if (allTreasureCells().size() != 8) {
            throw new IllegalStateException("Répartition invalide: doublons détectés entre cases de trésors");
        }

        if (!WARBOSS_CELLS.equals(immutableSet(64))) {
            throw new IllegalStateException("Répartition invalide: le Warboss doit être uniquement en case 64");
        }

        Set<Integer> allEnemies = allEnemyCells();
        Set<Integer> allTreasures = allTreasureCells();

        Set<Integer> intersection = new LinkedHashSet<>(allEnemies);
        intersection.retainAll(allTreasures);
        if (!intersection.isEmpty()) {
            throw new IllegalStateException("Répartition invalide: collisions ennemis/trésors détectées sur " + intersection);
        }
    }

    // Regroupe toutes les cases ennemies en un seul ensemble.
    public static Set<Integer> allEnemyCells() {
        Set<Integer> all = new LinkedHashSet<>();
        all.addAll(ORK_CELLS);
        all.addAll(SORCERER_CELLS);
        all.addAll(SQUIG_CELLS);
        all.addAll(WARBOSS_CELLS);
        return all;
    }

    // Regroupe toutes les cases trésor en un seul ensemble.
    public static Set<Integer> allTreasureCells() {
        Set<Integer> all = new LinkedHashSet<>();
        all.addAll(POTION_CELLS);
        all.addAll(BIG_POTION_CELLS);
        return all;
    }

    // Vérifie qu'aucune case de la liste ne sort des bornes 1..64.
    private static void validateRange(Set<Integer> cells, String label) {
        for (int cell : cells) {
            if (cell < 1 || cell > 64) {
                throw new IllegalStateException("Répartition invalide: " + label + " contient une case hors plateau: " + cell);
            }
        }
    }
}