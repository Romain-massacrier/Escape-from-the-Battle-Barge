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
    private static final Color TERMINAL_GREEN = new Color(0, 255, 120);

    /** Affiche l'écran titre et exécute le callback au moment d'Entrée. */
    public void show(Runnable onEnterPressed) {

        JFrame frame = new JFrame(UiText.APP_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        JLabel background = createBackgroundLabel();

        background.setLayout(null);
        frame.setContentPane(background);
        frame.pack();
        frame.setLocationRelativeTo(null);

        startTitleMusic();
        bindEnterKey(frame, onEnterPressed);
        frame.setVisible(true);
    }

    private JLabel createBackgroundLabel() {
        java.net.URL bgUrl = getClass().getResource("/images/backgrounds/title.png");
        if (bgUrl != null) {
            return new JLabel(new ImageIcon(bgUrl));
        }

        JLabel fallback = new JLabel(UiText.Title.FALLBACK, SwingConstants.CENTER);
        fallback.setPreferredSize(new Dimension(1280, 720));
        fallback.setOpaque(true);
        fallback.setBackground(Color.BLACK);
        fallback.setForeground(TERMINAL_GREEN);
        fallback.setFont(new Font("Monospaced", Font.BOLD, 26));
        return fallback;
    }

    private void startTitleMusic() {
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/audio/title.wav");
            if (audioSrc == null) return;

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            // Son optionnel: on continue sans musique si indisponible.
        }
    }

    private void bindEnterKey(JFrame frame, Runnable onEnterPressed) {
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) return;

                stopTitleMusic();
                frame.dispose();
                if (onEnterPressed != null) {
                    onEnterPressed.run();
                }
            }
        });
    }

    private void stopTitleMusic() {
        if (clip == null) return;

        clip.stop();
        clip.close();
    }
}