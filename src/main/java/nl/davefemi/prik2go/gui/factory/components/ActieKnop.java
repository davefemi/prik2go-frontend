package nl.davefemi.prik2go.gui.factory.components;

import java.awt.Color;

import javax.swing.JButton;

/**
 * Klasse die een actieknop respresenteert die hoort bij de functies 'open vestiging',
 * 'sluit vestiging' en 'visualizer'
 */
public class ActieKnop extends JButton {
        private static final long serialVersionUID = 1L;
        private static final String STATUSWISSEL = "statuswissel";
        private static final String SLUITVESTIGING = "Sluit vestiging";
        private static final String HEROPENVESTIGING = "Heropen vestiging" ;
        private static final String VISUALIZER = "Visualizer";
        
        /**
         * Constructor voor deze ActieKnop
         * @param text
         */
        private ActieKnop(String actie) {
                super(actie);
                this.setBackground(new Color(211,211,211));
                this.setFocusable(false); 
        }
        
        /**
         * Methode die een JButton met de tekst voor de wisselfunctie terugkeert.
         * @return wisselknop
         */
        public static ActieKnop getStatusWisselKnop() {
                ActieKnop wisselKnop = new ActieKnop(SLUITVESTIGING);
                wisselKnop.putClientProperty("actie", STATUSWISSEL);
                wisselKnop.setBackground(new Color(211,211,211));
                wisselKnop.setFocusable(false);
                wisselKnop.setVisible(false);
                return wisselKnop;
        }
        
        /**
         * Methode die een JButton met de tekst voor de visualizerfunctie terugkeert.
         * @return visualizerknop
         */
        public static ActieKnop getVisualizerKnop() {
                ActieKnop visualizerKnop = new ActieKnop(VISUALIZER);
                visualizerKnop.setBackground(new Color(211,211,211));
                visualizerKnop.setFocusable(false);
                return visualizerKnop;
        }
        
        /**
         * Methode die de wisselfunctie aanpast aan de vestigingstatus
         * @param open
         */
        public void switchActie(boolean open) {
                if (this.getClientProperty("actie").equals(STATUSWISSEL)) {
                        setText(open ? SLUITVESTIGING : HEROPENVESTIGING);
                }
        }
}
