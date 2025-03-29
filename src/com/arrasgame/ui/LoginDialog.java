package com.arrasgame.ui;

import com.arrasgame.auth.WindowsAuth;
import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private boolean authenticated = false;

    public LoginDialog(JFrame parent) {
        super(parent, "Connexion Windows", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Panel principal
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel des champs
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        JTextField usernameField = new JTextField(System.getProperty("user.name"));
        JPasswordField passwordField = new JPasswordField();

        inputPanel.add(new JLabel("Utilisateur:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Mot de passe:"));
        inputPanel.add(passwordField);

        // Bouton de connexion
        JButton loginButton = new JButton("Connexion");
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
                    "Échec de connexion",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(loginButton, BorderLayout.SOUTH);
        add(panel);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}