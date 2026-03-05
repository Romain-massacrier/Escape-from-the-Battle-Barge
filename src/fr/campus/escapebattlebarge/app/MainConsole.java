package fr.campus.escapebattlebarge.app;

import fr.campus.escapebattlebarge.game.core.Game;

/*
 * Cette classe est l'entrée de la version console du jeu.
 * Elle sert surtout pour tester rapidement le gameplay sans interface graphique.
 */
public class MainConsole {

    // Lance le jeu console.
    public static void main(String[] args) {
        new Game().start();
    }
}
