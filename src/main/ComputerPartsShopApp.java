package main;

import javax.swing.*;

import gui.MainWindow;

public class ComputerPartsShopApp{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }
}