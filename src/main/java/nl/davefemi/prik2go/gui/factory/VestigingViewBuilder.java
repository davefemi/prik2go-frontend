package nl.davefemi.prik2go.gui.factory;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import nl.davefemi.prik2go.gui.factory.components.ActieKnop;
import nl.davefemi.prik2go.gui.factory.components.Menu;
import nl.davefemi.prik2go.gui.factory.components.VestigingKnop;

/**
 * Deze klasse is verantwoordelijk voor het bouwen en beheren van de elementen
 * van de VestigingView.
 */
public class VestigingViewBuilder {
        private JMenuBar menuBar = null;
        private JPanel vestigingPaneel = null;
        private ActieKnop statusWisselKnop = null;
        private JPanel klantenPaneel = null;
        private JLabel klantKoptekstLabel = null;
        private JTextArea klantNummerArea = null;
        private JPanel actiePaneel = null;
        private JLabel totaalKlantenLabel = null;
        private boolean vestigingError = false;
        private final ActionListener vestigingKnopListener;
        private final ActionListener actieKnopListener;
        private final ActionListener visualizerKnopListener;
        private static final String GEENGEGEVENS = "Geen beschikbare gegevens";
        private static final String WELKOMSTTEKST = "Klik op een vestiging...";
        private static final String KLANTKOPTEKST = "Klantgegevens ";
        private static final String TOTAALKLANTTEKST = "Totaal aantal klanten: ";
        
        /**
         * Constructor voor deze builder klasse
         * @param vestigingKnopListener
         * @param actieKnopListener
         * @param visualizerKnopListener
         */
        public VestigingViewBuilder(ActionListener vestigingKnopListener, 
                        ActionListener actieKnopListener, 
                        ActionListener visualizerKnopListener) {
                this.vestigingKnopListener = vestigingKnopListener;
                this.actieKnopListener = actieKnopListener;
                this.visualizerKnopListener = visualizerKnopListener;
        }

        public JMenuBar getMenu(){
                if (menuBar == null ){
                        menuBar = new Menu();
                        System.setProperty("apple.laf.useScreenMenuBar", "true");
                }
                return menuBar;
        }

        /**
         * Vestiging paneel wordt opgebouwd en knoppen worden toegevoegd met labels die
         * horen bij vestigingen.
         * @return vestigingpaneel
         */
        public JPanel getVestigingPaneel(Map<String, Boolean> locaties) {
                if (vestigingPaneel == null) {
                        vestigingPaneel = new JPanel();
                        vestigingPaneel.setLayout(new GridLayout(0, 1));
                }
                voegVestigingKnoppenToe(locaties);
                return vestigingPaneel;
        
        }
        /**
         * Klantenpaneel wordt opgebouwd
         * @return klantenpaneel
         */
        public JPanel getKlantenPaneel() {
                if (klantenPaneel == null) {
                        klantenPaneel = new JPanel();
                        klantenPaneel.setLayout(new BorderLayout(0,20));
                        klantenPaneel.setBorder(BorderFactory.createEmptyBorder(10,10,10,0));
                        klantenPaneel.add(getKlantNummerPaneel(), BorderLayout.CENTER);
                        if (!vestigingError) {
                                klantenPaneel.add(getActiePaneel(), BorderLayout.SOUTH);
                        }
                }
                return klantenPaneel;
        }
        
        /**
         * Vlag om aan te geven dat vestigingen niet konden worden opgehaald
         * @param status
         */
        public void setVestigingError(boolean status) {
                vestigingError = status;
        }
        
        /**
         * Display wordt geupdate en voorzien van nieuwe data, vestigingspanelen worden gerefresht
         * @param locaties
         * @param gekozenLocatie
         * @param klantnummers
         * @param aantalKlanten
         */
        public void updateDisplay(Map<String, Boolean> locaties, String gekozenLocatie, List<Integer> klantnummers,
                        int aantalKlanten, boolean actie) {
                boolean status = locaties.get(gekozenLocatie);
                leegVelden();
                if(klantnummers.size() > 0) {
                        int nummer = 1;
                        for (int n : klantnummers) {
                                klantNummerArea.append("Klant " + nummer + ": " + String.valueOf(n) + '\n');
                                nummer++;
                        }   
                }
                else if(!status) {
                        klantNummerArea.setText("Deze vestiging is gesloten");
                }
                else {
                        klantNummerArea.setText("Geen klantnummers gevonden");
                }
                klantNummerArea.setVisible(true);
                klantKoptekstLabel.setText(KLANTKOPTEKST + gekozenLocatie + ": ");
                totaalKlantenLabel.setText(TOTAALKLANTTEKST + aantalKlanten);
                statusWisselKnop.switchActie(status);
                statusWisselKnop.setVisible(true);
                if (actie) updateVestigingKnoppen(locaties);
        }
        
        /**
         * Display wordt geupdate, vestigingspanelen worden gerefresht
         * @param locaties
         */
        public void updateDisplay(Map<String, Boolean> locaties) {
                updateVestigingKnoppen(locaties);
        }
        
        /**
         * Toewijzing van een error tekst aan het klantenveld bij een locatie.
         * @param locatie
         * @param tekst
         */
        public void displayFoutMelding(String locatie, String tekst) {
                if (klantNummerArea == null) {
                        klantNummerArea = getKlantNummerArea();
                        }
                klantNummerArea.setText(tekst);
                klantNummerArea.setVisible(true);
                klantKoptekstLabel.setText(KLANTKOPTEKST + locatie + ": ");
        }
        
        /**
         * Methode om het vestigingspaneel te voorzien van de locatienamen en hun status.
         * @param locaties
         */
        private void voegVestigingKnoppenToe(Map<String, Boolean> locaties) {
                vestigingPaneel.removeAll();
                for (Map.Entry<String, Boolean> locatie : locaties.entrySet()) {
                        JButton knop = new VestigingKnop(locatie.getKey(), locatie.getValue());
                        knop.addActionListener(vestigingKnopListener);
                        vestigingPaneel.add(knop);
                        }
                vestigingPaneel.revalidate();
                vestigingPaneel.repaint(); 
        }

        private void updateVestigingKnoppen(Map<String, Boolean> locaties ){
                Component[] knoppen = vestigingPaneel.getComponents();
                for (Component c : knoppen){
                        VestigingKnop knop = (VestigingKnop) c;
                        boolean oldStatus = (boolean) knop.getClientProperty("status");
                        boolean newStatus = locaties.get(knop.getClientProperty("vestiging"));
                        if (newStatus != (Boolean) oldStatus){
                                knop.setStatus(newStatus);
                        }
                }
        }
         
        /**
         * Creatie van tekstgebied waarin klantnummers kunnen worden getoond
         * @return
         */
        private JTextArea getKlantNummerArea() {
                if (klantNummerArea == null) {
                        klantNummerArea = new JTextArea();
                        klantNummerArea.setOpaque(true);
                        klantNummerArea.setTabSize(7);
                        klantNummerArea.setBackground(Color.WHITE);
                        klantNummerArea.setEditable(false);
                        klantNummerArea.setVisible(false);
                }
                if (vestigingError) {
                        klantNummerArea.setText(GEENGEGEVENS);
                        klantNummerArea.setVisible(true);
                }
                return klantNummerArea;
        }

        /**
         * Panel waarop klantnummer zichtbaar worden, wordt opgebouwd. 
         * @return klantnummerpaneel
         */
        private JPanel getKlantNummerPaneel() {
                JPanel klantNummerPaneel = new JPanel();
                klantNummerPaneel.setLayout(new BorderLayout(0,20));             
                ScrollPane schuifpaneel = new ScrollPane();
                schuifpaneel.add(getKlantNummerArea());
                klantNummerPaneel.add(getKlantKoptekstLabel(), BorderLayout.NORTH);
                klantNummerPaneel.add(schuifpaneel, BorderLayout.CENTER);
                klantNummerPaneel.add(getTotaalKlantenLabel(), BorderLayout.SOUTH);
                return klantNummerPaneel;
        }
        
        /**
         * Velden worden leeggemaakt ten behoeve van het weergeven van nieuwe informatie.
         */
        private void leegVelden() {
                klantNummerArea.setText("");
                totaalKlantenLabel.setText("");
        }
        
        /**
         * Koptekstlabel wordt gecreeerd
         * @return klantkoptekstlabel
         */
        private JLabel getKlantKoptekstLabel() {
                if (klantKoptekstLabel == null) {
                        klantKoptekstLabel = new JLabel();
                }
                String tekst = vestigingError ? "" : WELKOMSTTEKST;
                klantKoptekstLabel.setText(tekst);
                klantKoptekstLabel.setFont(new Font("Plain", Font.BOLD, 18));
                return klantKoptekstLabel;
        }
        

        /**
         * Creatie van JLabel voor het weergeven van klantenaantallen
         * @return totaalklantenlabel
         */
        private JLabel getTotaalKlantenLabel() {
                if (totaalKlantenLabel == null) {
                        totaalKlantenLabel = new JLabel();
                        totaalKlantenLabel.setFont(new Font("Plain", Font.BOLD, 14));
                }
                return totaalKlantenLabel;
        }
        
        /**
         * Creatie van paneel waarop de actieknoppen zichtbaar zijn
         * @return actiepaneel
         */
        private JPanel getActiePaneel() {
                if (actiePaneel == null) {
                        actiePaneel = new JPanel();
                        actiePaneel.setBorder(BorderFactory.createEmptyBorder(0,0,0,15));
                        actiePaneel.setLayout(new GridLayout (0,2));
                        voegActieKnoppenToe();
                }
                return actiePaneel;
        }
        
        /**
         * Methode om actieknoppen aan te maken. Een actieknop om de status van de actieve vestiging
         * aan te passen en een visualizerknop om de visualizer van de vestigingen te openen.
         */
        private void voegActieKnoppenToe() {
                statusWisselKnop = ActieKnop.getStatusWisselKnop();      
                statusWisselKnop.addActionListener(actieKnopListener);
                actiePaneel.add(statusWisselKnop);
                ActieKnop visualizerKnop = ActieKnop.getVisualizerKnop();
                visualizerKnop.addActionListener(visualizerKnopListener);
                actiePaneel.add(visualizerKnop);
                }
}
