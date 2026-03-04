package fr.campus.escapebattlebarge.ui;

// Import des composants Swing (fenêtres, labels, champs texte, etc.)
import javax.swing.*;

// Import des classes graphiques AWT (couleurs, polices, toolkit…)
import java.awt.*;

// Gestion des événements d’actions (boutons, touches clavier…)
import java.awt.event.ActionEvent;

// Permet de charger une ressource (image) depuis le classpath
import java.net.URL;

public class MenuScreen {

    /**
     * Affiche l'écran de menu principal.
     * @param onNewGame Runnable exécuté lorsque le joueur choisit "New Game"
     */
    public void show(Runnable onNewGame) {

        // Garantit que tout le code Swing s'exécute dans le thread graphique (EDT)
        SwingUtilities.invokeLater(() -> {

            AudioManager.startLoopAudio("/audio/intro.wav");

            // Création de la fenêtre principale
            JFrame frame = new JFrame("Escape from the Battle Barge");

            // Ferme complètement l'application quand on clique sur la croix
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            // Empêche le redimensionnement pour garder un affichage fixe
            frame.setResizable(false);

            // Chargement de l'image de fond depuis /resources/images/
            URL bgUrl = getClass().getResource("/images/game1.png");

            // Sécurité : si l'image est introuvable, on stoppe le programme
            if (bgUrl == null) {
                throw new IllegalStateException("Missing resource: /images/game1.png");
            }

            // Création d'une ImageIcon à partir de l'image
            ImageIcon bgIcon = new ImageIcon(bgUrl);

            // JLabel utilisé comme fond d’écran
            JLabel background = new JLabel(bgIcon);

            // On désactive le layout manager pour positionner les éléments en coordonnées absolues
            background.setLayout(null);

            // On définit ce JLabel comme contenu principal de la fenêtre
            frame.setContentPane(background);

            // ==============================
            // Définition de la zone centrale (écran vert du terminal)
            // ==============================

            int x = 420;   // Position horizontale de base
            int y = 515;   // Position verticale de base
            int w = 820;   // Largeur zone (non utilisé ici mais garde la logique de zone)
            int h = 620;   // Hauteur zone (idem)

            // ==============================
            // Texte des options
            // ==============================

            JLabel options = new JLabel("1  New Game      2  Quit");

            // Police style terminal rétro
            options.setFont(new Font("Monospaced", Font.BOLD, 26));

            // Couleur vert phosphorescent
            options.setForeground(new Color(0, 255, 120));

            // Positionnement absolu
            options.setBounds(x + 120, y + 200, 600, 40);

            // Ajout au fond
            background.add(options);

            // ==============================
            // Texte d'invite
            // ==============================

            JLabel prompt = new JLabel("Prononcez votre décision : ");

            prompt.setFont(new Font("Monospaced", Font.PLAIN, 18));
            prompt.setForeground(new Color(0, 255, 120));
            prompt.setBounds(x + 120, y + 260, 400, 30);

            background.add(prompt);

            // ==============================
            // Champ de saisie utilisateur
            // ==============================

            JTextField input = new JTextField();

            // Police plus grande pour lisibilité
            input.setFont(new Font("Monospaced", Font.BOLD, 22));

            // Texte vert
            input.setForeground(new Color(0, 255, 120));

            // Curseur vert
            input.setCaretColor(new Color(0, 255, 120));

            // Fond transparent pour laisser voir l'image
            input.setOpaque(false);

            // Bordure verte style terminal
            input.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 120), 2));

            // Positionnement
            input.setBounds(x + 420, y + 255, 120, 35);

            background.add(input);

            // ==============================
            // Configuration finale de la fenêtre
            // ==============================

            // Taille identique à l’image de fond
            frame.setSize(bgIcon.getIconWidth(), bgIcon.getIconHeight());

            // Centre la fenêtre à l’écran
            frame.setLocationRelativeTo(null);

            // ==============================
            // Logique de validation du choix
            // ==============================

            Runnable validate = () -> {

                // Récupère la valeur saisie
                String v = input.getText().trim();

                // Si choix 1 → démarre la partie
                if ("1".equals(v)) {
                    frame.dispose(); // Ferme le menu
                    if (onNewGame != null) onNewGame.run(); // Lance le jeu
                }
                // Si choix 2 → quitte l’application
                else if ("2".equals(v)) {
                    System.exit(0);
                }
                // Sinon → erreur sonore + reset du champ
                else {
                    Toolkit.getDefaultToolkit().beep();
                    input.setText("");
                }
            };

            // Validation quand l'utilisateur appuie sur Entrée dans le champ
            input.addActionListener(e -> validate.run());

            // ==============================
            // Gestion globale de la touche Entrée
            // ==============================

            JRootPane root = frame.getRootPane();

            // Associe la touche ENTER à une action personnalisée
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("ENTER"), "enter");

            // Définition de l’action associée
            root.getActionMap().put("enter", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validate.run();
                }
            });

            // Rend la fenêtre visible
            frame.setVisible(true);

            // Donne automatiquement le focus au champ texte
            SwingUtilities.invokeLater(input::requestFocusInWindow);
        });
    }
}