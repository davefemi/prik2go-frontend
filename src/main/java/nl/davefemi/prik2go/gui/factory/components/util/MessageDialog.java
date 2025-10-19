package nl.davefemi.prik2go.gui.factory.components.util;

import javax.swing.*;
import java.awt.*;

public abstract class MessageDialog {
        private static boolean infoDialog = false;
        private static boolean errorDialog = false;
        
        /**
         * Genereert een berichtdialoog ter informatie
         * @param parent bijbehorende container, indien van toepassing
         * @param bericht te vertonen 
         */
        public static void getInfoDialog(Container parent, String bericht) {
                if (!infoDialog) {
                        Toolkit.getDefaultToolkit().beep();
                                infoDialog = true;
                                JOptionPane.showMessageDialog(parent, bericht, "Message", JOptionPane.INFORMATION_MESSAGE);
                                infoDialog = false;
                }
        }      
        
        /**
         * Genereert een foutmelding
         * @param parent bijbehorende container, indien van toepassing
         * @param error te vertonen
         */
        public static void getErrorDialog(Container parent, String error) {
                if (!errorDialog) {
                        Toolkit.getDefaultToolkit().beep();
                                errorDialog = true;
                                JOptionPane.showMessageDialog(parent, error, "Error", JOptionPane.ERROR_MESSAGE);
                                errorDialog = false;
                }
        }   
        
}
