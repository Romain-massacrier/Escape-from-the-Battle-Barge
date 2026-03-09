package fr.campus.escapebattlebarge.ui.screen;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;

/** Écran titre avec musique, fermé via Entrée. */
public class TitleScreen {

    private Clip clip;

    /** Affiche l'écran titre et exécute le callback au moment d'Entrée. */
    public void show(Runnable onEnterPressed) {

        JFrame frame = new JFrame("Escape from the Battle Barge");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

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
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            // Son optionnel: on continue sans musique si indisponible.
        }

        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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