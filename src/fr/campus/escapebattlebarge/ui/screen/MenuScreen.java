package fr.campus.escapebattlebarge.ui.screen;

import fr.campus.escapebattlebarge.ui.audio.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

/*
 * Cette classe affiche le menu graphique principal (New Game / Quit).
 * Elle est appelée après l'intro pour laisser le joueur démarrer ou quitter.
 * Entrées: saisie clavier. Sorties: lancement partie ou fermeture appli.
 */
public class MenuScreen {

    /**
     * Affiche l'écran de menu principal.
     * @param onNewGame Runnable exécuté lorsque le joueur choisit "New Game"
     */
    // Affiche le menu et attend que le joueur choisisse 1 ou 2.
    public void show(Runnable onNewGame) {

        // On passe par l'EDT pour garder une UI stable.
        SwingUtilities.invokeLater(() -> {

            AudioManager.startLoopAudio("/audio/intro.wav");

            JFrame frame = new JFrame("Escape from the Battle Barge");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // Fond du menu principal.
            URL bgUrl = getClass().getResource("/images/zones/game1.png");
            if (bgUrl == null) {
                throw new IllegalStateException("Missing resource: /images/game1.png");
            }
            ImageIcon bgIcon = new ImageIcon(bgUrl);
            JLabel background = new JLabel(bgIcon);
            background.setLayout(null);
            frame.setContentPane(background);

            int x = 420;
            int y = 515;

            JLabel options = new JLabel("1  New Game      2  Quit");
            options.setFont(new Font("Monospaced", Font.BOLD, 26));
            options.setForeground(new Color(0, 255, 120));
            options.setBounds(x + 120, y + 200, 600, 40);
            background.add(options);

            JLabel prompt = new JLabel("Prononcez votre décision : ");

            prompt.setFont(new Font("Monospaced", Font.PLAIN, 18));
            prompt.setForeground(new Color(0, 255, 120));
            prompt.setBounds(x + 120, y + 260, 400, 30);

            background.add(prompt);

            JTextField input = new JTextField();
            input.setFont(new Font("Monospaced", Font.BOLD, 22));
            input.setForeground(new Color(0, 255, 120));
            input.setCaretColor(new Color(0, 255, 120));
            input.setOpaque(false);
            input.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 120), 2));
            input.setBounds(x + 420, y + 255, 120, 35);

            background.add(input);
            frame.setSize(bgIcon.getIconWidth(), bgIcon.getIconHeight());
            frame.setLocationRelativeTo(null);

            Runnable validate = () -> {
                String v = input.getText().trim();

                if ("1".equals(v)) {
                    frame.dispose();
                    if (onNewGame != null) onNewGame.run();
                }
                else if ("2".equals(v)) {
                    System.exit(0);
                }
                else {
                    // Ici on bip et on vide, tant que le joueur ne tape pas 1 ou 2.
                    Toolkit.getDefaultToolkit().beep();
                    input.setText("");
                }
            };

            input.addActionListener(e -> validate.run());

            JRootPane root = frame.getRootPane();
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("ENTER"), "enter");
            root.getActionMap().put("enter", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validate.run();
                }
            });

            frame.setVisible(true);
            SwingUtilities.invokeLater(input::requestFocusInWindow);
        });
    }
}