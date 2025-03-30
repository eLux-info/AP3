package com.arrasgame.ui;

import com.arrasgame.database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.geom.RoundRectangle2D;

public class ForfaitApp extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(48, 63, 159);
    private static final Color SECONDARY_COLOR = new Color(255, 87, 34);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);

    private JTextField codeField;
    private JLabel countdownLabel;
    private Timer timer;
    private int remainingTime;
    private JButton pauseButton;
    private CardLayout cardLayout;
    private JPanel cards;

    public ForfaitApp() {
        setTitle("ArrasGame - Gestion de Forfait");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 800, 600, 20, 20));

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setOpaque(false);

        cards.add(createInputPanel(), "INPUT");
        cards.add(createTimerPanel(), "TIMER");

        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(cards, BorderLayout.CENTER);

        add(mainPanel);
        cardLayout.show(cards, "INPUT");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel title = new JLabel("ARRASGAME");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);

        JButton closeButton = createIconButton("×", new Color(239, 83, 80));
        closeButton.addActionListener(e -> System.exit(0));

        controlPanel.add(closeButton);
        
        header.add(title, BorderLayout.WEST);
        header.add(controlPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel title = new JLabel("Activer votre forfait");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        gbc.gridy = 0;
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("Entrez le code de votre forfait");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_COLOR.brighter());
        gbc.gridy = 1;
        panel.add(subtitle, gbc);

        codeField = new JTextField(20);
        styleTextField(codeField);
        gbc.gridy = 2;
        panel.add(codeField, gbc);

        JButton verifyButton = createStyledButton("VÉRIFIER LE CODE", SECONDARY_COLOR, 200);
        verifyButton.addActionListener(e -> checkForfait(codeField.getText()));
        gbc.gridy = 3;
        panel.add(verifyButton, gbc);

        return panel;
    }

    private JPanel createTimerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));

        countdownLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        countdownLabel.setForeground(PRIMARY_COLOR);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        controlPanel.setOpaque(false);

        pauseButton = createStyledButton("⏸ PAUSE", SECONDARY_COLOR, 150);
        pauseButton.addActionListener(e -> togglePause());

        JButton returnButton = createStyledButton("◀ RETOUR", PRIMARY_COLOR, 150);
        returnButton.addActionListener(e -> returnToMenu());

        controlPanel.add(pauseButton);
        controlPanel.add(returnButton);

        panel.add(countdownLabel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String text, Color color, int width) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.WHITE);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(width, 50));
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        
        return button;
    }

    private JButton createIconButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setForeground(color);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(color);
            }
        });
        
        return button;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(300, 45));
    }
    

    private void returnToMenu() {
        updateRemainingTimeInDatabase();
        
        if (timer != null) timer.stop();
        if (autoSaveTimer != null) autoSaveTimer.stop();
        
        cardLayout.show(cards, "INPUT");
        
        JOptionPane.showMessageDialog(
            this,
            "Temps sauvegardé : " + formatTime(remainingTime),
            "Sauvegarde réussie",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void checkForfait(String code) {
        String query = "SELECT remaining_time FROM user_packages WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                remainingTime = rs.getInt("remaining_time");
                
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                
                cardLayout.show(cards, "TIMER");
                startCountdown();
            } else {
                JOptionPane.showMessageDialog(this, "Code invalide ou forfait expiré", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Timer autoSaveTimer;

    private void startCountdown() {
        // Timer principal
        timer = new Timer(1000, e -> {
            if (remainingTime > 0) {
                remainingTime--;
                
                countdownLabel.setText(
                    String.format("%02d:%02d:%02d",
                        remainingTime / 3600,
                        (remainingTime % 3600) / 60,
                        remainingTime % 60)
                );
                
                if (shouldSaveToDatabase()) {
                    updateRemainingTimeInDatabase();
                }
            } else {
                timer.stop();
                countdownLabel.setText("00:00:00");
                updateRemainingTimeInDatabase();
            }
        });
        timer.start();

        autoSaveTimer = new Timer(15_000, e -> updateRemainingTimeInDatabase());
        autoSaveTimer.start();
    }
    
    private boolean shouldSaveToDatabase() {
        return (remainingTime <= 0) || (!timer.isRunning());
    }

    private void togglePause() {
        if (timer.isRunning()) {
            timer.stop();
            updateRemainingTimeInDatabase();
            pauseButton.setText("▶ Reprendre");
        } else {
            timer.start();
            pauseButton.setText("⏸ Pause");
        }
    }

    private void updateRemainingTimeInDatabase() {
        String query = "UPDATE user_packages SET remaining_time = ? WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, remainingTime);
            pstmt.setString(2, codeField.getText());
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);

            if (login.isAuthenticated()) {
                ForfaitApp app = new ForfaitApp();
                app.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }

}
