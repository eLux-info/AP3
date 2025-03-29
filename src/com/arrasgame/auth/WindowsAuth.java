package com.arrasgame.auth;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Win32Exception;

public class WindowsAuth {
    public static boolean authenticate(String username, String password) {
        try {
            // Nouvelle méthode pour JNA 5.12+
            return Advapi32.INSTANCE.LogonUser(
                username,
                null, // Domaine (null pour local)
                password,
                Advapi32.LOGON32_LOGON_NETWORK,
                Advapi32.LOGON32_PROVIDER_DEFAULT,
                null
            );
            return true;
        } catch (Win32Exception e) {
            // Échec d'authentification
            return false;
        }
    }
}