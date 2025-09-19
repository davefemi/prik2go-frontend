package nl.davefemi.prik2go.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import javax.swing.*;
import nl.davefemi.prik2go.Prik2GoApp;
import nl.davefemi.prik2go.controller.CustomerViewController;
import nl.davefemi.prik2go.gui.factory.components.util.ActiveWindow;
import nl.davefemi.prik2go.gui.factory.components.util.BerichtDialoog;
import nl.davefemi.prik2go.exceptions.VestigingException;
import nl.davefemi.prik2go.gui.factory.components.util.SwingBringToFront;
import nl.davefemi.prik2go.gui.factory.CustomerViewBuilder;
import nl.davefemi.prik2go.observer.ApiObserver;
import nl.davefemi.prik2go.observer.ApiSubject;

/**
 * Klasse die verantwoordelijk is voor de grafisch vertonen
 * van de vestiging- en klantendata.
 */
public class CustomerView extends JFrame implements ApiObserver {
        private static final long serialVersionUID = 1L;
        private static final Logger logger = Logger.getLogger(CustomerView.class.getName());
        private final CustomerViewController controller;
        private final CustomerViewBuilder builder;
        private static final Dimension FRAME_SIZE = new Dimension (520, 570);
        private static final Point FRAME_LOC = new Point (100,100);
        private static final String TITEL = "Vestigingen";
        private String geselecteerdeLocatie = null;
        private final CustomerView view = this;

        /**
         * Constructor waarin de controller wordt geïnitialiseerd en vestigingsklassen worden
         * opgehaald. Er wordt een builder-klasse geïnitialiseerd die verantwoordelijk is voor
         * het opbouwen en beheren van de verschillende frame-onderdelen.
         */
        public CustomerView(CustomerViewController controller) {
                super();
                this.setFocusable(true);
                this.controller = controller;
                this.builder = new CustomerViewBuilder(new VestigingKnopListener(),
                                new ActieKnopListener(), 
                                new VisualizerKnopListener());
                initialize();
                initData();
        }
        
        /**
         * Titel en formaat van container worden ingesteld, het opbouwen van het frame
         * wordt verder geïnitialiseerd.
         */
        private void initialize() {
                this.getRootPane().putClientProperty("cardName", "CustomerView");
                this.setTitle(TITEL);
                this.setSize(FRAME_SIZE);
                this.setLocation(FRAME_LOC);
                this.setLayout(new BorderLayout());
                this.add(builder.getLoading(), BorderLayout.CENTER);
        }

        private void initData(){
                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                                CountDownLatch latch = new CountDownLatch(1);
                                Boolean[] res = new Boolean[1];
                                controller.getVestigingStatus(vestigingMap ->{
                                        view.add(builder.getVestigingPaneel(vestigingMap), BorderLayout.WEST);
                                        view.setJMenuBar(builder.getMenu());
                                        view.add(builder.getKlantenPaneel(), BorderLayout.CENTER);
                                        res[0] = true;
                                        latch.countDown();
                                }, exception->{res[0] = false; latch.countDown();});
                                latch.await();
                                return res[0];
                        }

                        @Override
                        protected void done(){
                                view.remove(builder.getLoading());
                                try {
                                        if (get()) {
                                                view.revalidate();
                                                view.repaint();
                                                logger.info("App initialized");
                                        }
                                        else {
                                                logger.info("App init false");
                                                builder.setVestigingError(true);
                                        }
                                } catch (Exception e) {
                                        logger.info("App init exception");
                                        builder.setVestigingError(true);
                                }
                                finally {
                                        view.revalidate();
                                        view.repaint();
                                }
                        }
                };
                worker.execute();
        }


        public void bringToFront() {
                SwingBringToFront.bringWindowToFront(this);
        }
         

        /**
         * Klantgegevens worden opgehaald bij de geselecteerde locatie. Als er geen locatie
         * gekozen is, dan wordt er een aanroep gedaan naar het builder-object met alleen de
         * vestigingen. Als er een fout optreedt, wordt dat weergegeven.
         * @param locatie van de geselecteerde vestiging
         */
        private void updateDisplay(String locatie, boolean actie) {
                if (locatie != null) {
                        geselecteerdeLocatie = locatie;
                        controller.getKlantenDTO(locatie, klanten -> controller.getVestigingStatus(vestigingMap->{
                                builder.updateDisplay(vestigingMap, locatie, klanten.getKlantNummers(),
                                        klanten.getAantalKlanten(), actie);
                                this.revalidate();
                                this.repaint();
                        }, e -> logger.warning(e.getMessage())),
                                e -> {logger.warning(e.getMessage());
                                builder.displayFoutMelding(locatie,
                                        e instanceof IllegalAccessException
                                                ?"No authorisation"
                                                :"An error occurred"
                                );
                        });
                }
                else {
                        controller.getVestigingStatus(builder::updateDisplay,
                                ex -> logger.warning(ex.getMessage()));
                }
        }

        /**
         * Bij notificatie van VestigingSubject object wordt de methode aangeroepen om het display
         * te updaten. Als er geen open vestigingen zijn, wordt daarvan een melding weergegeven.
         */
        @Override
        public void update(ApiSubject s, Object arg) {
                updateDisplay(geselecteerdeLocatie, true);
        }
        
        /**
         * Luisteraar implementatie voor de vestiging-knop.
         */
        private class VestigingKnopListener implements ActionListener {
                
                /**
                 * Actie bij een event gegenereerd door deze JButton. Display wordt ge-updatet aan de
                 * hand van de vestiging die hoort bij deze knop.
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                        ActiveWindow.setActiveComponent(view);
                        updateDisplay((String) ((JButton) e.getSource()).getClientProperty("vestiging"), false);
                }
        }
        
        /**
         * Luisteraar implementatie voor de wisselknop.
         */
        private class ActieKnopListener implements ActionListener {

                /**
                 * Actie bij een event gegenereerd door deze JButton. Status van de vestiging die hoort bij 
                 * deze knop wordt veranderd.
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                        ActiveWindow.setActiveComponent(view);
                                controller.veranderVestigingStatus(geselecteerdeLocatie, ex ->{
                                        if (ex instanceof VestigingException){
                                                BerichtDialoog.getInfoDialoog(getContentPane(), ex.getLocalizedMessage());
                                        }
                                        else {
                                                BerichtDialoog.getErrorDialoog(getContentPane(), ex.getMessage());
                                        }
                                });
                }
        }
        
        /**
         * Luisteraar implementatie voor de visualiseringsknop.
         */
        private class VisualizerKnopListener implements ActionListener {

                /**
                 * Actie bij een event gegenereerd door deze JButton. De VisualizerView wordt opgestart.
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                        setLocation(5, getLocation().y);
                        Prik2GoApp.startVisualisatie();       
                }     
        }

}

