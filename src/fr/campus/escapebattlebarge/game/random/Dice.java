package fr.campus.escapebattlebarge.game.random;

import java.util.Random;

/*
 * Cette classe centralise les tirages aléatoires du jeu (dé et bornes min/max).
 * Elle est utilisée partout où une règle dépend du hasard.
 * Entrées: bornes de tirage. Sorties: entier aléatoire.
 */
public class Dice {
    private final Random rng = new Random();

    // Renvoie un résultat de dé classique entre 1 et 6.
    public int rollD6() {
        return 1 + rng.nextInt(6);
    }

    // Renvoie un entier entre min et max (bornes incluses).
    public int between(int min, int max) {
        // ATTENTION : si max < min, on renvoie min pour éviter une erreur de tirage.
        if (max < min) return min;
        return min + rng.nextInt(max - min + 1);
    }
}
