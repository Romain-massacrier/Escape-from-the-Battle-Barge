package fr.campus.escapebattlebarge.ui;

// Gestion du son (Clip, AudioInputStream, AudioSystem…)
import javax.sound.sampled.*;

// Composants graphiques Swing
import javax.swing.*;

// Outils graphiques AWT (Layout, etc.)
import java.awt.*;

// Gestion des événements clavier
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Lecture de flux (audio depuis resources)
import java.io.BufferedInputStream;
import java.io.InputStream;

public class TitleScreen {

    // Clip audio utilisé pour jouer la musique du titre
    private Clip clip;

    /**
     * Affiche l'écran titre.
     * @param onEnterPressed Runnable exécuté lorsque le joueur appuie sur ENTER
     */
    public void show(Runnable onEnterPressed) {

        // Création de la fenêtre principale
        JFrame frame = new JFrame("Escape from the Battle Barge");

        // Ferme uniquement cette fenêtre (et non toute l'application)
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Empêche le redimensionnement
        frame.setResizable(false);

        // ==============================
        // Chargement de l'image de fond
        // ==============================

        // Récupération de l'image depuis le dossier resources/images
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/title.png"));

        // JLabel utilisé comme conteneur graphique avec image en arrière-plan
        JLabel background = new JLabel(bgIcon);

        // Layout nul pour placement en coordonnées absolues
        background.setLayout(null);

        // Définit le fond comme contenu principal de la fenêtre
        frame.setContentPane(background);

        // Ajuste automatiquement la taille de la fenêtre à celle de l’image
        frame.pack();

        // Centre la fenêtre sur l’écran
        frame.setLocationRelativeTo(null);

        // ==============================
        // 🎵 Lecture de la musique du titre
        // ==============================

        try {
            // Charge le fichier audio depuis resources/audio
            InputStream audioSrc = getClass().getResourceAsStream("/audio/title.wav");

            // Buffer pour optimiser la lecture
            InputStream bufferedIn = new BufferedInputStream(audioSrc);

            // Conversion en AudioInputStream lisible par le système audio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            // Récupère un Clip audio (mémoire courte, adapté aux sons courts ou musiques)
            clip = AudioSystem.getClip();

            // Charge le flux audio dans le clip
            clip.open(audioStream);

            // Joue en boucle infinie
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            // En cas d'erreur (fichier manquant, format invalide…)
            e.printStackTrace();
        }

        // ==============================
        // Gestion de l’appui sur une touche
        // ==============================

        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                // Si la touche pressée est ENTER
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    // Arrêt propre de la musique si elle existe
                    if (clip != null) {
                        clip.stop();   // Stoppe la lecture
                        clip.close();  // Libère la mémoire audio
                    }

                    // Ferme la fenêtre titre
                    frame.dispose();

                    // Lance la suite du jeu si une action est définie
                    if (onEnterPressed != null) {
                        onEnterPressed.run();
                    }
                }
            }
        });

        // Rend la fenêtre visible
        frame.setVisible(true);
    }
}