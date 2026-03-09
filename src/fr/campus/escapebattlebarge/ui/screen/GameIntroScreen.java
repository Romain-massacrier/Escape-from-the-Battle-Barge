package fr.campus.escapebattlebarge.ui.screen;

import fr.campus.escapebattlebarge.ui.audio.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

/*
 * Cette classe affiche les pages de narration d'introduction.
 * Elle fait défiler le texte à chaque Entrée, puis lance la suite du jeu.
 * Entrées: touche Entrée. Sorties: page suivante ou callback de fin.
 */
public class GameIntroScreen {

    /**
     * Affiche l’écran d’introduction avec plusieurs pages de texte.
     *
     * @param pages       Tableau contenant les différentes pages de narration
     * @param onFinished  Action exécutée lorsque l’introduction est terminée
     */
    // Affiche l'intro, avance page par page, puis lance la suite.
    public void show(String[] pages, Runnable onFinished) {

        // On passe par l'EDT pour éviter les soucis d'affichage Swing.
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(UiText.APP_TITLE);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setResizable(false);

            URL bgUrl = getClass().getResource("/images/backgrounds/game.png");
            if (bgUrl == null) {
                throw new IllegalStateException("Missing resource: /images/game.png");
            }

            ImageIcon bgIcon = new ImageIcon(bgUrl);

            // JLabel utilisé comme conteneur principal avec image
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

            storyArea.setBounds(Position.GameIntro.STORY_BOUNDS);

            background.add(storyArea);
            frame.setSize(bgIcon.getIconWidth(), bgIcon.getIconHeight());
            frame.setLocationRelativeTo(null);

            final int[] pageIndex = {0};

            if (pages != null && pages.length > 0) {
                storyArea.setText(pages[0]);
            }

            AudioManager.startLoopAudio("/audio/intro.wav");

            JRootPane root = frame.getRootPane();
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("ENTER"), "next");

            root.getActionMap().put("next", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pageIndex[0]++;

                    if (pages != null && pageIndex[0] < pages.length) {
                        storyArea.setText(pages[pageIndex[0]]);
                    }
                    else {
                        frame.dispose();

                        if (onFinished != null) onFinished.run();
                    }
                }
            });

            frame.setVisible(true);
            frame.requestFocusInWindow();
        });
    }
}