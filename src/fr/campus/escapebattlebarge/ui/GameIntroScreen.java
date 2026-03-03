package fr.campus.escapebattlebarge.ui;

// Gestion du système audio (Clip, AudioSystem, etc.)
import javax.sound.sampled.*;

// Composants graphiques Swing
import javax.swing.*;

// Classes graphiques AWT (Color, Font…)
import java.awt.*;

// Gestion des actions (InputMap / ActionMap)
import java.awt.event.ActionEvent;

// Lecture de flux audio
import java.io.BufferedInputStream;
import java.io.InputStream;

// Permet de charger une image depuis le classpath
import java.net.URL;

public class GameIntroScreen {

    // Clip audio utilisé pour la musique d’introduction
    private Clip clip;

    /**
     * Affiche l’écran d’introduction avec plusieurs pages de texte.
     *
     * @param pages       Tableau contenant les différentes pages de narration
     * @param onFinished  Action exécutée lorsque l’introduction est terminée
     */
    public void show(String[] pages, Runnable onFinished) {

        // S'assure que l'interface Swing s'exécute dans le thread graphique (EDT)
        SwingUtilities.invokeLater(() -> {

            // Création de la fenêtre
            JFrame frame = new JFrame("Escape from the Battle Barge");

            // Ferme uniquement cette fenêtre
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            // Empêche le redimensionnement
            frame.setResizable(false);

            // ==============================
            // Chargement du fond d'écran
            // ==============================

            URL bgUrl = getClass().getResource("/images/game.png");

            // Sécurité si ressource absente
            if (bgUrl == null) {
                throw new IllegalStateException("Missing resource: /images/game.png");
            }

            ImageIcon bgIcon = new ImageIcon(bgUrl);

            // JLabel utilisé comme conteneur principal avec image
            JLabel background = new JLabel(bgIcon);

            // Layout null pour positionnement manuel
            background.setLayout(null);

            frame.setContentPane(background);

            // ==============================
            // Zone de texte narrative
            // ==============================

            JTextArea storyArea = new JTextArea();

            // Rend le texte non éditable
            storyArea.setEditable(false);

            // Active retour à la ligne automatique
            storyArea.setLineWrap(true);

            // Coupe les lignes proprement sur les mots
            storyArea.setWrapStyleWord(true);

            // Couleur vert terminal
            storyArea.setForeground(new Color(0, 255, 120));

            // Police monospaced pour effet rétro
            storyArea.setFont(new Font("Monospaced", Font.PLAIN, 20));

            // Fond transparent pour voir l’image derrière
            storyArea.setOpaque(false);

            // ==============================
            // Définition de la zone centrale
            // ==============================

            int x = 420;
            int y = 240;
            int w = 820;
            int h = 620;

            storyArea.setBounds(x, y, w, h);

            background.add(storyArea);

            // Ajuste la taille de la fenêtre à l’image
            frame.setSize(bgIcon.getIconWidth(), bgIcon.getIconHeight());

            // Centre la fenêtre
            frame.setLocationRelativeTo(null);

            // ==============================
            // Gestion des pages
            // ==============================

            // Tableau pour permettre modification dans classe anonyme
            final int[] pageIndex = {0};

            // Affiche la première page si disponible
            if (pages != null && pages.length > 0) {
                storyArea.setText(pages[0]);
            }

            // ==============================
            // Lancement musique d’introduction
            // ==============================

            AudioManager.startLoopAudio("/audio/intro.wav");

            // ==============================
            // Gestion touche ENTER pour passer page
            // ==============================

            JRootPane root = frame.getRootPane();

            // Associe ENTER à l’action "next"
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("ENTER"), "next");

            // Définition de l’action associée
            root.getActionMap().put("next", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // Passe à la page suivante
                    pageIndex[0]++;

                    // Si encore des pages → affiche suivante
                    if (pages != null && pageIndex[0] < pages.length) {
                        storyArea.setText(pages[pageIndex[0]]);
                    }
                    // Sinon → fin de l’introduction
                    else {
                        frame.dispose();

                        if (onFinished != null) onFinished.run();
                    }
                }
            });

            // Rend la fenêtre visible
            frame.setVisible(true);

            // Donne le focus à la fenêtre pour capter ENTER
            frame.requestFocusInWindow();
        });
    }

    /**
     * Lance une musique en boucle.
     * @param path Chemin vers le fichier audio dans resources
     */
    private void startLoopAudio(String path) {

        try {
            // Chargement du fichier audio
            InputStream audioSrc = getClass().getResourceAsStream(path);

            // Vérification ressource
            if (audioSrc == null) {
                throw new IllegalStateException("Missing resource: " + path);
            }

            // Buffer pour optimiser lecture
            InputStream bufferedIn = new BufferedInputStream(audioSrc);

            // Conversion en AudioInputStream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            // Création du Clip
            clip = AudioSystem.getClip();

            // Charge le son en mémoire
            clip.open(audioStream);

            // Lecture en boucle infinie
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stoppe proprement la musique et libère la mémoire.
     */
    private void stopAudio() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }
}