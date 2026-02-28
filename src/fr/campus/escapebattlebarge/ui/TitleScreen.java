package fr.campus.escapebattlebarge.ui;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class TitleScreen {

    private Clip clip;

    public void show(Runnable onEnterPressed) {

        JFrame frame = new JFrame("Escape from the Battle Barge");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setResizable(false);

        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/title.png"));
        JLabel background = new JLabel(bgIcon);
        background.setLayout(null);
        frame.setContentPane(background);

        frame.pack();
        frame.setLocationRelativeTo(null);

        // 🎵 Musique du titre
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/audio/title.wav");
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