package com.arrasgame.ui;

import com.arrasgame.database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ForfaitApp extends JFrame {
    private JTextField codeField;
    private JLabel countdownLabel;
    private Timer timer;
    private int remainingTime;
    private JButton pauseButton;
    private CardLayout cardLayout;
    private JPanel cards;

    public ForfaitApp() {
        setTitle("Gestion de Forfait");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.add(createInputPanel(), "INPUT");
        cards.add(createTimerPanel(), "TIMER");
        
        add(cards);
        cardLayout.show(cards, "INPUT");
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel codeLabel = new JLabel("Entrez votre code de forfait :");
        codeField = new JTextField(15);
        JButton verifyButton = new JButton("Vérifier");

        codeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        codeField.setFont(new Font("Arial", Font.PLAIN, 14));
        verifyButton.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0;
        panel.add(codeLabel, gbc);
        
        gbc.gridy = 1;
        panel.add(codeField, gbc);
        
        gbc.gridy = 2;
        panel.add(verifyButton, gbc);

        verifyButton.addActionListener(e -> checkForfait(codeField.getText()));

        return panel;
    }

    private JPanel createTimerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        countdownLabel = new JLabel("00:00", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Digital-7", Font.BOLD, 80));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pauseButton = new JButton("⏸ Pause");
        JButton returnButton = new JButton("◀ Retour au menu");

        pauseButton.setFont(new Font("Arial", Font.BOLD, 16));
        returnButton.setFont(new Font("Arial", Font.BOLD, 16));
        pauseButton.setBackground(new Color(255, 193, 7));
        returnButton.setBackground(new Color(33, 150, 243));
        pauseButton.setForeground(Color.BLACK);
        returnButton.setForeground(Color.WHITE);

        pauseButton.addActionListener(e -> togglePause());
        returnButton.addActionListener(e -> returnToMenu());

        buttonPanel.add(pauseButton);
        buttonPanel.add(returnButton);
        
        panel.add(countdownLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void returnToMenu() {
        updateRemainingTimeInDatabase();
        
        if (timer != null) {
            timer.stop();
        }
        
        cardLayout.show(cards, "INPUT");
        
        JOptionPane.showMessageDialog(this, 
                "Temps sauvegardé : " + formatTime(remainingTime),
                "Sauvegarde réussie", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    
    private String formatTime(int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
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

    private int lastSavedTime = -1;
    
    private void startCountdown() {
        timer = new Timer(1000, e -> {
            if (remainingTime > 0) {
                remainingTime--;
                
                countdownLabel.setText(
                    String.format("%02d:%02d", remainingTime / 60, remainingTime % 60)
                );
                
                if (shouldSaveToDatabase()) {
                    updateRemainingTimeInDatabase();
                    lastSavedTime = remainingTime;
                }
            } else {
                timer.stop();
                countdownLabel.setText("00:00");
                updateRemainingTimeInDatabase();
            }
        });
        timer.start();
    }
    
    private boolean shouldSaveToDatabase() {
        return (lastSavedTime - remainingTime >= 15) ||
               (remainingTime <= 0) ||
               (!timer.isRunning());
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
            // D'abord afficher le login
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
            
            // Si authentification OK, lancer l'app principale
            if (login.isAuthenticated()) {
                ForfaitApp app = new ForfaitApp();
                app.setVisible(true);
            } else {
                System.exit(0); // Fermer si échec
            }
        });
    }

}