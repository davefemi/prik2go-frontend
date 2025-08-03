package nl.davefemi.prik2go.controller;

import com.fasterxml.jackson.databind.node.BooleanNode;
import nl.davefemi.prik2go.dto.KlantenDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import nl.davefemi.prik2go.service.DataServiceInterface;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Deze klasse is verantwoordelijk voor de controller functie.
 * Het object beheert de vestigingsobjecten en verzorgt de
 * creatie en communicatie met de mapper klasse.
 */
public class CustomerViewController {
        private final DataServiceInterface service;
        private List<String> vestigingen = null;

        /**
         * Constructor van de controller. Mapper wordt gecreëerd en
         * de vestigingen worden geïnitialiseerd
         * @throws ApplicatieException als er een fout optreedt met de verbinding met de
         * database
         */
        public CustomerViewController(DataServiceInterface service) throws ApplicatieException {
                this.service = service;
                initVestigingen();
        }
        
        /**
         * Verzorgt de initialisatie van de Vestiging-arraylist.
         */
        private void initVestigingen() throws ApplicatieException {
                vestigingen = service.getVestigingen();
        }

        
        /**
         * Geeft een lijst met de vestigingslocaties terug.
         * @return List<String> plaatsnamen
         */
        public List<String> getVestigingLocaties() {
                return vestigingen;
        }
        
                
        /**
         * Geeft de KlantenDTO voor de gegeven locatie terug. Als er geen locatie gekozen is, of de 
         * terugkeerwaarde van de dto is null, wordt een exceptie opgegooid.
         * @param locatie van de vestiging
         * @return KlantenDTO met klantgegevens voor de gegeven locatie
         */
        public void getKlantenDTO(String locatie, Consumer<KlantenDTO> callback, Consumer<Exception> exceptionConsumer) {
                SwingWorker worker = new SwingWorker<KlantenDTO, Void>() {
                        @Override
                        protected KlantenDTO doInBackground() throws Exception {
                                return service.getKlantenDTO(locatie);
                        }

                        @Override
                        protected void done(){
                            try {
                                KlantenDTO result = (KlantenDTO) get();
                                callback.accept(result);
                            } catch (Exception e) {
                                exceptionConsumer.accept((Exception) e.getCause());
                            }
                        }
                };
                worker.execute();
        }
        
        /**
         * Geeft de status van de vestiging terug Als de vestiging open is, zal de status 'true'
         * zijn. Als een vestiging gesloten is, zal de status 'false' zijn.
         * @return boolean
         */
        public void getVestigingStatus2(String locatie, Consumer<Boolean> callback, Consumer<Exception> exceptionConsumer)  {
                SwingWorker worker = new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                                return service.getVestigingStatus(locatie);
                        }

                        @Override
                        protected void done(){
                                try {
                                        boolean result = (Boolean) get();
                                        callback.accept(result);
                                }
                                catch (Exception e){
                                        exceptionConsumer.accept((Exception) e.getCause());
                                }
                        }
                };
                worker.execute();
        }

        public void getVestigingStatus(Consumer<Map<String, Boolean>> callback, Consumer<Exception> exceptionHandling) {
                SwingWorker worker = new SwingWorker<List<Future<Boolean>>, Void>() {
                        @Override
                        protected List<Future<Boolean>> doInBackground() throws Exception {
                                ExecutorService pool = Executors.newFixedThreadPool(Math.min(12, vestigingen.size()));
                                List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
                                for (String v : vestigingen) {
                                        Future future = pool.submit(() ->
                                                service.getVestigingStatus(v));
                                        futures.add(future);
                                }
                                pool.shutdown();
                                return futures;
                        }

                        @Override
                        protected void done() {
                                try {
                                        List<Future<Boolean>> futures = (List<Future<Boolean>>) get();
                                        Map<String, Boolean> nieuweMap = new TreeMap<String, Boolean>();
                                        for (int i = 0; i<futures.size(); i++) {
                                                nieuweMap.put(vestigingen.get(i), futures.get(i).get());
                                        }
                                        callback.accept(nieuweMap);
                                } catch (Exception e) {
                                        exceptionHandling.accept((Exception) e.getCause());
//                                        logger.warning("Fout opgetreden: " + e.getMessage());
                                }
                        }
                };
                worker.execute();
        }
        
        /**
         * Methode zal aan de hand van de huidige vestigingsstatus kiezen tussen een sluiting
         * of heropening van de gegeven locatie.
         * @throws VestigingException als alle vestigingen status 'gesloten' bereiken wordt deze
         * exceptie opgegooid
         */
        public void veranderVestigingStatus(String locatie, Consumer<Exception> exceptionHandling) {
                SwingWorker worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                                service.veranderVestigingStatus(locatie);
                            return null;
                        }

                        @Override
                        protected void done(){
                            try {
                                get();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                    exceptionHandling.accept((Exception) e);
                            }
                        }
                };
                worker.execute();

        }
}
