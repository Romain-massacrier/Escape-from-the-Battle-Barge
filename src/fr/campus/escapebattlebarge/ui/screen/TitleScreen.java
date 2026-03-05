package fr.campus.escapebattlebarge.ui.screen;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;

/*
 * Cette classe affiche l'écran titre avec musique en boucle.
 * Elle attend Entrée pour fermer l'écran et lancer la suite du jeu.
 * Entrée: touche clavier. Sortie: fermeture fenêtre + callback onEnterPressed.
 */
public class TitleScreen {

    // Garde la musique du titre pour pouvoir l'arrêter proprement.
    private Clip clip;

    /**
     * Affiche l'écran titre.
     * @param onEnterPressed Runnable exécuté lorsque le joueur appuie sur ENTER
     */
    // Affiche l'écran titre et continue quand le joueur appuie sur Entrée.
    public void show(Runnable onEnterPressed) {

        JFrame frame = new JFrame("Escape from the Battle Barge");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        // On charge l'image de fond; si absente, on met un écran texte de secours.
        java.net.URL bgUrl = getClass().getResource("/images/backgrounds/title.png");
        JLabel background;
        if (bgUrl != null) {
            ImageIcon bgIcon = new ImageIcon(bgUrl);
            background = new JLabel(bgIcon);
        } else {
            background = new JLabel("Escape from the Battle Barge - Appuyez sur Entrée", SwingConstants.CENTER);
            background.setPreferredSize(new Dimension(1280, 720));
            background.setOpaque(true);
            background.setBackground(Color.BLACK);
            background.setForeground(new Color(0, 255, 120));
            background.setFont(new Font("Monospaced", Font.BOLD, 26));
        }

        background.setLayout(null);
        frame.setContentPane(background);
        frame.pack();
        frame.setLocationRelativeTo(null);

        try {
            InputStream audioSrc = getClass().getResourceAsStream("/audio/title.wav");
            // ATTENTION : si title.wav manque, le catch gère l'erreur et on continue sans son.
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // On coupe la musique avant de passer à la suite.
                    if (clip != null) {
                        clip.stop();
                        clip.close();
                    }
                    frame.dispose();
                    if (onEnterPressed != null) {
                        onEnterPressed.run();
                    }
                }
            }
        });
        frame.setVisible(true);
    }
}