package com.arrasgame.ui;

import com.arrasgame.auth.WindowsAuth;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.*;

public class LoginDialog extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(48, 63, 159);
    private static final Color SECONDARY_COLOR = new Color(255, 87, 34);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    
    private boolean authenticated = false;
    private Point initialClick;

    public LoginDialog(JFrame parent) {
        super(parent, "", true);
        setUndecorated(true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, 500, 400, 25, 25));

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(getWidth(), 60));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel title = new JLabel("ARRASGAME");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        closeButton.setForeground(new Color(239, 83, 80));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 105, 97));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(new Color(239, 83, 80));
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(closeButton, BorderLayout.EAST);

        header.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel loginTitle = new JLabel("Authentification Windows");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginTitle.setForeground(TEXT_COLOR);
        contentPanel.add(loginTitle, gbc);

        gbc.gridy++;
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 15, 20));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JTextField usernameField = new JTextField(System.getProperty("user.name"));
        styleTextField(usernameField);
        JPasswordField passwordField = new JPasswordField();
        styleTextField(passwordField);

        inputPanel.add(createInputGroup("Nom d'utilisateur:", usernameField));
        inputPanel.add(createInputGroup("Mot de passe:", passwordField));

        contentPanel.add(inputPanel, gbc);

        gbc.gridy++;
        JButton loginButton = createStyledButton("CONNEXION", SECONDARY_COLOR, 250);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (WindowsAuth.authenticate(username, password)) {
                authenticated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Identifiants Windows incorrects",
                    "Échec de l'authentification",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        contentPanel.add(loginButton, gbc);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createInputGroup(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 8));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(TEXT_COLOR);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void styleTextField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setPreferredSize(new Dimension(350, 50));
    }

    private JButton createStyledButton(String text, Color color, int width) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int borderInset = 2;
                g2.setColor(getModel().isPressed() ? color.darker() :
                             getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(borderInset, borderInset, getWidth() - borderInset * 2, getHeight() - borderInset * 2, 12, 12);

                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, textX, textY);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(getFont());
                int buttonHeight = fm.getHeight() + 25;
                return new Dimension(width, Math.max(buttonHeight, 60));
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBorder(null);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, 60));
        
        return button;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
