package com.senac;



import com.senac.service.Menu;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Menu().setVisible(true);
        });
    }
}
