package fr.campus.escapebattlebarge.ui.screen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.function.Consumer;

/** Écran Swing de sélection de classe puis saisie du nom du héros. */
public class CharacterSelectScreen {

    private enum Step {
        SELECT_CLASS,
        ENTER_NAME
    }

    // Affiche l'écran et renvoie le nom saisi via le callback de la classe choisie.
    public void show(Consumer<String> onMarineSelected, Consumer<String> onLibrarianSelected) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame(UiText.CharacterSelect.FRAME_TITLE);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setResizable(false);

            URL bgUrl = getClass().getResource("/images/zones/characterselect.png");
            if (bgUrl == null) {
                throw new IllegalStateException("Missing resource: /images/characterselect.png");
            }

            ImageIcon bgIcon = new ImageIcon(bgUrl);
            JLabel background = new JLabel(bgIcon);
            background.setLayout(null);
            frame.setContentPane(background);

            frame.setSize(bgIcon.getIconWidth(), bgIcon.getIconHeight());
            frame.setLocationRelativeTo(null);

            Color green = new Color(0, 255, 120);

                addInfoArea(background, green, Position.CharacterSelect.MARINE_INFO_BOUNDS, UiText.CharacterSelect.MARINE_INFO);
                addInfoArea(background, green, Position.CharacterSelect.LIBRARIAN_INFO_BOUNDS, UiText.CharacterSelect.LIBRARIAN_INFO);

            JPanel terminal = new JPanel(null);
            terminal.setBounds(Position.CharacterSelect.TERMINAL_BOUNDS);
            terminal.setOpaque(false);
            background.add(terminal);

            JLabel line1 = makeLabel(UiText.CharacterSelect.TERMINAL_SELECT_TITLE, green, "Monospaced", Font.BOLD, 18);
            line1.setBounds(Position.CharacterSelect.TERMINAL_LINE1_BOUNDS);
            terminal.add(line1);

            JLabel line2 = makeLabel(UiText.CharacterSelect.TERMINAL_SELECT_OPTIONS, green, "Monospaced", Font.PLAIN, 18);
            line2.setBounds(Position.CharacterSelect.TERMINAL_LINE2_BOUNDS);
            terminal.add(line2);

            JLabel line3 = makeLabel(UiText.CharacterSelect.TERMINAL_SELECT_PROMPT, green, "Monospaced", Font.PLAIN, 18);
            line3.setBounds(Position.CharacterSelect.TERMINAL_LINE3_BOUNDS);
            terminal.add(line3);

            JTextField input = new JTextField();
            input.setBounds(Position.CharacterSelect.INPUT_BOUNDS);
            input.setFont(new Font("Monospaced", Font.BOLD, 18));
            input.setForeground(green);
            input.setCaretColor(green);
            input.setBackground(new Color(0, 0, 0, 0));
            input.setOpaque(false);
            input.setBorder(BorderFactory.createLineBorder(green, 1));
            terminal.add(input);

            final Step[] step = { Step.SELECT_CLASS };
            final int[] selected = { 1 };

            Runnable switchToNameStep = () -> {
                step[0] = Step.ENTER_NAME;

                line1.setText(UiText.CharacterSelect.TERMINAL_NAME_TITLE);
                line2.setText(selected[0] == 1 ? UiText.CharacterSelect.TERMINAL_CLASS_MARINE : UiText.CharacterSelect.TERMINAL_CLASS_LIBRARIAN);
                line3.setText(UiText.CharacterSelect.TERMINAL_NAME_PROMPT);

                input.setText("");
                input.requestFocusInWindow();
            };

            Runnable validate = () -> {
                String v = input.getText().trim();

                if (step[0] == Step.SELECT_CLASS) {
                    handleClassSelection(v, selected, input, switchToNameStep);
                    return;
                }

                if (!isValidCharacterName(v)) return;

                finalizeSelection(frame, selected[0], v, onMarineSelected, onLibrarianSelected);
            };

            input.addActionListener(e -> validate.run());

            // Raccourcis clavier pour jouer vite.
            JRootPane root = frame.getRootPane();

            bindShortcut(root, "1", "marine", () -> {
                if (step[0] != Step.SELECT_CLASS) return;
                input.setText("1");
                selected[0] = 1;
                input.requestFocusInWindow();
            });

            bindShortcut(root, "2", "librarian", () -> {
                if (step[0] != Step.SELECT_CLASS) return;
                input.setText("2");
                selected[0] = 2;
                input.requestFocusInWindow();
            });

            bindShortcut(root, "ENTER", "validate", validate);
            bindShortcut(root, "ESCAPE", "quit", () -> {
                frame.dispose();
                System.exit(0);
            });

            frame.setVisible(true);
            input.requestFocusInWindow();
        });
    }

    private void addInfoArea(JLabel background, Color green, Rectangle bounds, String text) {
        JTextArea area = makeInfoArea(green, 18, true);
        area.setBounds(bounds);
        area.setText(text);
        background.add(area);
    }

    private void bindShortcut(JRootPane root, String keyStroke, String actionId, Runnable action) {
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(keyStroke), actionId);
        root.getActionMap().put(actionId, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void handleClassSelection(String value, int[] selected, JTextField input, Runnable switchToNameStep) {
        if ("1".equals(value)) {
            selected[0] = 1;
            switchToNameStep.run();
            return;
        }

        if ("2".equals(value)) {
            selected[0] = 2;
            switchToNameStep.run();
            return;
        }

        Toolkit.getDefaultToolkit().beep();
        input.setText("");
    }

    private boolean isValidCharacterName(String name) {
        if (!name.isEmpty()) return true;

        Toolkit.getDefaultToolkit().beep();
        return false;
    }

    private void finalizeSelection(JFrame frame, int selectedClass, String name,
                                   Consumer<String> onMarineSelected,
                                   Consumer<String> onLibrarianSelected) {
        frame.dispose();

        if (selectedClass == 1) {
            if (onMarineSelected != null) onMarineSelected.accept(name);
            return;
        }

        if (onLibrarianSelected != null) onLibrarianSelected.accept(name);
    }

    // Crée une zone texte de présentation avec le style terminal vert.
    private JTextArea makeInfoArea(Color green, int fontSize, boolean transparent) {
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setForeground(green);
        a.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
        a.setOpaque(!transparent);
        a.setBorder(new EmptyBorder(6, 6, 6, 6));
        return a;
    }

    // Crée un label avec couleur/police personnalisées.
    private JLabel makeLabel(String text, Color color, String fontName, int style, int size) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font(fontName, style, size));
        return l;
    }
}