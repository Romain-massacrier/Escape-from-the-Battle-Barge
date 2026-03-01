package fr.campus.escapebattlebarge.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public class MenuScreen {

    public void show(Runnable onNewGame) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Escape from the Battle Barge");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(false);

            URL bgUrl = getClass().getResource("/images/game1.png");
            if (bgUrl == null) {
                throw new IllegalStateException("Missing resource: /images/game1.png");
            }

            ImageIcon bgIcon = new ImageIcon(bgUrl);
            JLabel background = new JLabel(bgIcon);
            background.setLayout(null);
            frame.setContentPane(background);

            // Zone centrale (écran vert)
            int x = 420;
            int y = 515;
            int w = 820;
            int h = 620;

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
                } else if ("2".equals(v)) {
                    System.exit(0);
                } else {
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