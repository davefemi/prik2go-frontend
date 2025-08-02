package nl.davefemi.prik2go.gui.factory.components;

import java.awt.Color;

import javax.swing.JButton;

/**
 * Subklasse van JButton voor de aanmaak van een vestigingknop.
 */
public class VestigingKnop extends JButton {
        private static final long serialVersionUID = 1L;
        private static final Color OPENKLEUR = new Color (6,64,43);
        private static final Color GESLOTENKLEUR = new Color(149,6,6);
        
        /**
         * Constructor voor de klasse VestigingKnop
         * @param locatie van de vestiging
         * @param open: status van de vestiging
         */
        public VestigingKnop(String locatie, boolean open) {
                super(locatie);
                this.setForeground(Color.WHITE);
                this.setFocusable(false);
                this.putClientProperty("vestiging", locatie);
                this.putClientProperty("status", open);
                this.setBackground(open ? OPENKLEUR : GESLOTENKLEUR);
        }

        public void setStatus(boolean status){
                this.putClientProperty("status", status);
                this.setBackground(status ? OPENKLEUR : GESLOTENKLEUR);
        }
}
