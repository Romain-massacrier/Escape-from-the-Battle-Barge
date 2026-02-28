package fr.campus.escapebattlebarge.ui;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class GameIntroScreen {

    private Clip clip;

    public void show(String[] pages, Runnable onFinished) {
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Escape from the Battle Barge");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setResizable(false);

            URL bgUrl = getClass().getResource("/images/game.png");
            if (bgUrl == null) {
                throw new IllegalStateException("Missing resource: /images/game.png");
            }

            ImageIcon bgIcon = new ImageIcon(bgUrl);
            JLabel background = new JLabel(bgIcon);
            background.setLayout(null);
            frame.setContentPane(background);

            JTextArea storyArea = new JTextArea();
            storyArea.setEditable(false);
            storyArea.setLineWrap(true);
            storyArea.setWrapStyleWord(true);
            storyArea.setForeground(new Color(0, 255, 120));
            storyArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
            storyArea.setOpaque(false);

            int x = 420;
            int y = 240;
            int w = 820;
            int h = 620;

            storyArea.setBounds(x, y, w, h);
            background.add(storyArea);

            frame.setSize(bgIcon.getIconWidth(), bgIcon.getIconHeight());
            frame.setLocationRelativeTo(null);

            final int[] pageIndex = {0};
            storyArea.setText(pages[pageIndex[0]]);

            startLoopAudio("/audio/intro.wav");

            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        pageIndex[0]++;

                        if (pageIndex[0] < pages.length) {
                            storyArea.setText(pages[pageIndex[0]]);
                        } else {
                            stopAudio();
                            frame.dispose();
                            if (onFinished != null) {
                                onFinished.run();
                            }
                        }
                    }
                }
            });

            frame.setVisible(true);
            frame.requestFocusInWindow();
        });
    }

    private void startLoopAudio(String path) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(path);
            if (audioSrc == null) {
                throw new IllegalStateException("Missing resource: " + path);
            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }
}