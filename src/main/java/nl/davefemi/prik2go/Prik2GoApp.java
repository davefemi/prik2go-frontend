package nl.davefemi.prik2go;

import nl.davefemi.prik2go.authentication.Authenticator;
import nl.davefemi.prik2go.client.ApiClient;
import nl.davefemi.prik2go.controller.VisualizerController;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.BerichtDialoog;
import nl.davefemi.prik2go.gui.CustomerView;
import nl.davefemi.prik2go.gui.VisualizerView;
import nl.davefemi.prik2go.controller.CustomerViewController;
import nl.davefemi.prik2go.observer.ApiSubject;
import nl.davefemi.prik2go.service.DataService;
import nl.davefemi.prik2go.service.DataServiceInterface;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Deze klasse is verantwoordelijk voor het opstarten van de applicatie
 * en creatie van de controllers en gui's. 
 */
public class Prik2GoApp {
        private static final Logger logger = Logger.getLogger(Prik2GoApp.class.getName());
        private static VisualizerView visualizerView;
        private static ApiSubject apiSubject;
        private static DataServiceInterface service;

        /**
         * Main methode. Als er een foutmelding optreedt, zal een error dialoog worden getoond.
         * @param args
         */
        public static void main (String [] args) {
                init();
        }

        private static void init(){
                logger.info("Applicatie wordt opgestart");
                apiSubject = new ApiClient(new RestTemplate());
                service = new DataService((ApiClient) apiSubject);
                try {
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                        boolean result = Authenticator.ensureValidSession();

                        if (!result){
                                System.exit(0);
                        }
                        startSessie();
                }
                catch (Exception e) {
                        if(e instanceof UnsupportedLookAndFeelException) {
                                logger.warning(e.getMessage());
                        }
                        BerichtDialoog.getErrorDialoog(null, e.getMessage());
                        System.exit(0);
                }
        }

        /**
         * Methode voor het opstarten van de visualizer view. Als er al een visualizer view is geïnitialiseerd,
         * wordt deze zichtbaar. Bij een exceptie wordt een error dialoog getoond.
         */
        public static void startVisualisatie() {
                try {
                if (visualizerView == null) {
                        visualizerView = new VisualizerView(new VisualizerController(service));
                }              
                SwingUtilities.invokeLater(() -> {
                        VisualizerView gui = visualizerView;
                        apiSubject.attach(gui);
                        gui.setVisible(true);
                        gui.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                });
                }
                catch (Exception e) {
                        BerichtDialoog.getErrorDialoog(null, e.getMessage());
                }
        }
        
        /**
         * Methode voor het opstarten van de sessie met de creatie van de VestigingController
         * en de VestigingView.
         */
        private static void startSessie() throws ApplicatieException, IllegalAccessException {
                                CustomerViewController customerViewController = new CustomerViewController(service);
                                SwingUtilities.invokeLater(() -> {
                                        CustomerView gui = new CustomerView(customerViewController);
                                        gui.setVisible(true);
                                        apiSubject.attach(gui);
                                        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                                });
        }


}
