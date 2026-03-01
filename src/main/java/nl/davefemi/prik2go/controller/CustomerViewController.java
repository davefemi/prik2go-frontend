package nl.davefemi.prik2go.controller;

import nl.davefemi.prik2go.dto.BranchDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
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
        private List<String> branches = null;

        /**
         * Constructor van de controller. Mapper wordt gecreëerd en
         * de vestigingen worden geïnitialiseerd
         * @throws ApplicationException als er een fout optreedt met de verbinding met de
         * database
         */
        public CustomerViewController(DataServiceInterface service) throws ApplicationException, IllegalAccessException {
                this.service = service;
                initBranches();
        }
        
        /**
         * Verzorgt de initialisatie van de Vestiging-arraylist.
         */
        private void initBranches() throws ApplicationException, IllegalAccessException {
                branches = service.getBranches();
        }

        
        /**
         * Geeft een lijst met de vestigingslocaties terug.
         * @return List<String> plaatsnamen
         */
        public List<String> getVestigingLocaties() {
                return branches;
        }
        
                
        /**
         * Geeft de KlantenDTO voor de gegeven locatie terug. Als er geen locatie gekozen is, of de 
         * terugkeerwaarde van de dto is null, wordt een exceptie opgegooid.
         * @param locatie van de vestiging
         */
        public void getKlantenDTO(String locatie, Consumer<BranchDTO> callback, Consumer<Exception> exceptionConsumer) {
                SwingWorker<BranchDTO, Void> worker = new SwingWorker<>() {
                        @Override
                        protected BranchDTO doInBackground() throws Exception {
                                return service.getCustomerDTO(locatie);
                        }

                        @Override
                        protected void done(){
                            try {
                                BranchDTO result = get();
                                callback.accept(result);
                            } catch (Exception e) {
                                exceptionConsumer.accept((Exception) e.getCause());
                            }
                        }
                };
                worker.execute();
        }

        public void getBranchStatus(Consumer<Map<String, Boolean>> callback, Consumer<Exception> exceptionHandling) {
                SwingWorker<List<Future<Boolean>>, Void> worker = new SwingWorker<>() {
                        @Override
                        protected List<Future<Boolean>> doInBackground() {

                                ExecutorService pool = Executors.newFixedThreadPool(Math.min(12, branches.size()));
                                List<Future<Boolean>> futures = new ArrayList<>();
                                for (String v : branches) {
                                        Future<Boolean> future = pool.submit(() ->
                                                service.getBranchStatus(v));
                                        futures.add(future);
                                }
                                pool.shutdown();
                                return futures;
                        }

                        @Override
                        protected void done() {
                                try {
                                        List<Future<Boolean>> futures = get();
                                        Map<String, Boolean> nieuweMap = new TreeMap<>();
                                        for (int i = 0; i<futures.size(); i++) {
                                                nieuweMap.put(branches.get(i), futures.get(i).get());
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
         */
        public void veranderVestigingStatus(String locatie, Consumer<Exception> exceptionHandling) {
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                                service.changeBranchStatus(locatie);
                            return null;
                        }

                        @Override
                        protected void done(){
                            try {
                                get();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                    exceptionHandling.accept((Exception) e.getCause());
                            }
                        }
                };
                worker.execute();

        }
}
