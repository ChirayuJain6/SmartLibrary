package main;

import ui.MainFrame;
import javax.swing.*;

/**
 * Main.java — The entry point of SmartLibrary.
 *
 * This is the file that contains the main() method.
 * It sets the look-and-feel and launches the main window.
 *
 * To run:
 *   In VS Code terminal (from SmartLibrary/ folder):
 *     javac -d out src/models/*.java src/services/*.java src/ui/*.java src/main/*.java
 *     java -cp out main.Main
 */
public class Main {

    public static void main(String[] args) {

        // Set the Nimbus Look & Feel for a modern appearance
        // Nimbus is built into Java — no external libraries needed!
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());

                    // Customize Nimbus colors to match our dark-sidebar design
                    UIManager.put("nimbusBase",           new java.awt.Color(59, 130, 246));   // Blue
                    UIManager.put("nimbusBlueGrey",       new java.awt.Color(241, 245, 249));  // Light background
                    UIManager.put("control",              new java.awt.Color(241, 245, 249));  // Control bg
                    UIManager.put("Table.alternateRowColor", new java.awt.Color(248, 250, 252));
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to system look & feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Last resort: default Java look & feel — always available
            }
        }

        // SwingUtilities.invokeLater ensures UI is created on the Event Dispatch Thread (EDT)
        // This is the correct and thread-safe way to start a Swing application
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
