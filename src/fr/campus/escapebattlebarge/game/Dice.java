package fr.campus.escapebattlebarge.game;

import java.util.Random;

public class Dice {
    private final Random rng = new Random();

    public int rollD6() {
        return 1 + rng.nextInt(6);
    }

    public int between(int min, int max) {
        if (max < min) return min;
        return min + rng.nextInt(max - min + 1);
    }
}
