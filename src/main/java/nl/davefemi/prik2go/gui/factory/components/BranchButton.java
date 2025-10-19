package nl.davefemi.prik2go.gui.factory.components;

import java.awt.Color;

import javax.swing.JButton;

/**
 * Subklasse van JButton voor de aanmaak van een vestigingknop.
 */
public class BranchButton extends JButton {
        private static final long serialVersionUID = 1L;
        private static final Color OPENKLEUR = new Color (187, 214, 236);
        private static final Color GESLOTENKLEUR = new Color(236, 231, 231);
        private static final Color LETTERKLEUR = new Color(1,1,1);
        
        /**
         * Constructor voor de klasse VestigingKnop
         * @param locatie van de vestiging
         * @param open: status van de vestiging
         */
        public BranchButton(String locatie, boolean open) {
                super(locatie);
                this.setForeground(LETTERKLEUR);
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
