package fr.campus.escapebattlebarge.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.function.Consumer;

public class CharacterSelectScreen {

    private enum Step {
        SELECT_CLASS,
        ENTER_NAME
    }

    public void show(Consumer<String> onMarineSelected, Consumer<String> onLibrarianSelected) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Select Your Astartes");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setResizable(false);

            URL bgUrl = getClass().getResource("/images/characterselect.png");
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

            // =========================================================
            // POSITIONS
            // =========================================================

            int marineX = 620;
            int marineY = 280;
            int marineW = 560;
            int marineH = 180;

            int librX = 620;
            int librY = 510;
            int librW = 560;
            int librH = 180;

            int termX = 390;
            int termY = 700;
            int termW = 880;
            int termH = 120;

            int inputX = 620;
            int inputY = 50;
            int inputW = 120;
            int inputH = 28;

            // =========================================================
            // TEXTES
            // =========================================================

            String marineText =
                    "SPACE MARINE\n\n" +
                            "Il avance dans le sang et ne s’arrête jamais.\n\n" +
                            "Armements: Bolter, Chainsword.\n" +
                            "Attaque : +5 - Vie : +10\n";

            String librarianText =
                    "LIBRARIAN\n\n" +
                            "Sa pensée tranche plus profond qu’aucune lame.\n\n" +
                            "Armements : Bâton de force, Tempête psychique.\n" +
                            "Attaque : +8 - Vie : +6\n";

            // =========================================================
            // UI: Textes de présentation
            // =========================================================

            JTextArea marineInfo = makeInfoArea(green, 18, true);
            marineInfo.setBounds(marineX, marineY, marineW, marineH);
            marineInfo.setText(marineText);
            background.add(marineInfo);

            JTextArea librarianInfo = makeInfoArea(green, 18, true);
            librarianInfo.setBounds(librX, librY, librW, librH);
            librarianInfo.setText(librarianText);
            background.add(librarianInfo);

            // =========================================================
            // UI: Console
            // =========================================================

            JPanel terminal = new JPanel(null);
            terminal.setBounds(termX, termY, termW, termH);
            terminal.setOpaque(false);
            background.add(terminal);

            JLabel line1 = makeLabel("Console d'Auspex : sélection d'Astartes", green, "Monospaced", Font.BOLD, 18);
            line1.setBounds(18, 10, termW - 36, 20);
            terminal.add(line1);

            JLabel line2 = makeLabel("1  Space Marine       2  Librarian", green, "Monospaced", Font.PLAIN, 18);
            line2.setBounds(18, 32, termW - 36, 20);
            terminal.add(line2);

            JLabel line3 = makeLabel("Entrez 1 ou 2, puis scellez votre choix par Entrée :", green, "Monospaced", Font.PLAIN, 18);
            line3.setBounds(18, 55, termW - 36, 18);
            terminal.add(line3);

            JTextField input = new JTextField();
            input.setBounds(inputX, inputY, inputW, inputH);
            input.setFont(new Font("Monospaced", Font.BOLD, 18));
            input.setForeground(green);
            input.setCaretColor(green);
            input.setBackground(new Color(0, 0, 0, 0));
            input.setOpaque(false);
            input.setBorder(BorderFactory.createLineBorder(green, 1));
            terminal.add(input);

            // =========================================================
            // LOGIQUE: 2 ETAPES
            // =========================================================

            final Step[] step = { Step.SELECT_CLASS };
            final int[] selected = { 1 };

            Runnable switchToNameStep = () -> {
                step[0] = Step.ENTER_NAME;

                line1.setText("Console d'Auspex : identité du guerrier");
                line2.setText(selected[0] == 1 ? "Classe : Space Marine" : "Classe : Librarian");
                line3.setText("Entrez le nom du personnage, puis appuyez sur Entrée :");

                input.setText("");
                input.requestFocusInWindow();
            };

            Runnable validate = () -> {
                String v = input.getText().trim();

                if (step[0] == Step.SELECT_CLASS) {

                    if ("1".equals(v)) {
                        selected[0] = 1;
                        switchToNameStep.run();
                    } else if ("2".equals(v)) {
                        selected[0] = 2;
                        switchToNameStep.run();
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                        input.setText("");
                    }

                    return;
                }

                // Step ENTER_NAME
                if (v.isEmpty()) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                String finalName = v;

                frame.dispose();

                if (selected[0] == 1) {
                    if (onMarineSelected != null) onMarineSelected.accept(finalName);
                } else {
                    if (onLibrarianSelected != null) onLibrarianSelected.accept(finalName);
                }
            };

            input.addActionListener(e -> validate.run());

            // Key bindings
            JRootPane root = frame.getRootPane();

            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("1"), "marine");
            root.getActionMap().put("marine", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (step[0] != Step.SELECT_CLASS) return;
                    input.setText("1");
                    selected[0] = 1;
                    input.requestFocusInWindow();
                }
            });

            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("2"), "librarian");
            root.getActionMap().put("librarian", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (step[0] != Step.SELECT_CLASS) return;
                    input.setText("2");
                    selected[0] = 2;
                    input.requestFocusInWindow();
                }
            });

            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("ENTER"), "validate");
            root.getActionMap().put("validate", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validate.run();
                }
            });

            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke("ESCAPE"), "quit");
            root.getActionMap().put("quit", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    System.exit(0);
                }
            });

            frame.setVisible(true);
            input.requestFocusInWindow();
        });
    }

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

    private JLabel makeLabel(String text, Color color, String fontName, int style, int size) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font(fontName, style, size));
        return l;
    }
}