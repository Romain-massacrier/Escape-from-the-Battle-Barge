package fr.campus.escapebattlebarge.game.random;

import java.util.Random;

/** Utilitaire de tirages aléatoires du jeu. */
public class Dice {
    private final Random rng = new Random();

    /** Renvoie un résultat de dé entre 1 et 6. */
    public int rollD6() {
        return 1 + rng.nextInt(6);
    }

    /** Renvoie un entier entre min et max (inclus). */
    public int between(int min, int max) {
        if (max < min) return min;
        return min + rng.nextInt(max - min + 1);
    }
}
