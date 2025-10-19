package nl.davefemi.prik2go.client;

import nl.davefemi.prik2go.controller.AuthController;
import nl.davefemi.prik2go.dto.CustomerDTO;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import nl.davefemi.prik2go.gui.factory.components.util.ActiveWindow;
import nl.davefemi.prik2go.gui.factory.components.util.MessageDialog;
import nl.davefemi.prik2go.observer.ApiSubject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ApiClient extends ApiSubject implements ApiClientInterface {
    private static final Log log = LogFactory.getLog(ApiClient.class);
    private final Timer timer = new Timer(1000, new RefreshListener());
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://prik2go-backend.onrender.com/private/locations/%s";
//    private static final String BASE_URL = "http://localhost:8080/private/locations/%s";

    public ApiClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
        init();
    }

    private void init(){
        startTimer();
    }

    private void startTimer(){
        timer.start();
    }

    private void stopTimer(){
        timer.stop();
    }

    private synchronized HttpEntity<String> getHttpRequest() throws IllegalAccessException {
        AtomicBoolean ready = new AtomicBoolean(false);
        if (!AuthController.isSessionValid())
                stopTimer();
            SwingUtilities.invokeLater(()-> {
                try {
                    AuthController.validateSession();
                } catch (Exception e) {
                        MessageDialog.getErrorDialog((Container) ActiveWindow.getActiveComponent(), "No authorisation");
                        ready.set(true);
                }
            });
        SessionDTO session = null;
        HttpHeaders headers = new HttpHeaders();
            while(!ready.get()) {
                if (AuthController.isSessionValid()) {
                    session = AuthController.getSession();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("Authorization", "Bearer " + session.getToken());
                    ready.set(true);
                    startTimer();
                }
            }
            if (session == null){
                throw new IllegalAccessException("No authorisation");
            }
        return new HttpEntity<>(session.getUser().toString(), headers);
    }

    @Override
    public ResponseEntity<List> getBranches() throws IllegalAccessException, ApplicationException {
        try {
            return restTemplate.exchange(String.format(BASE_URL, "get-branches"),
                    HttpMethod.POST,
                    getHttpRequest(),
                    List.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicationException(e.getResponseBodyAsString());
        }
        catch (RestClientException e){
            throw new ApplicationException(e.getCause().getMessage());
        }
    }

    @Override
    public ResponseEntity<CustomerDTO> getCustomers(String location) throws IllegalAccessException {
        return restTemplate.exchange(String.format(BASE_URL, "get-customers?location=" + location),
                        HttpMethod.POST,
                        getHttpRequest(),
                        CustomerDTO.class);
    }

    @Override
    public ResponseEntity<Boolean> getBranchStatus(String location) throws ApplicationException, IllegalAccessException {
        try {
            return restTemplate.exchange(String.format(BASE_URL, "get-status?location=" + location),
                    HttpMethod.POST,
                    getHttpRequest(),
                    Boolean.class);
        }
        catch (HttpClientErrorException e){
            throw new ApplicationException(e.getResponseBodyAsString());
        }
    }

    @Override
    public void changeBranchStatus(String location) throws ApplicationException, IllegalAccessException {
        if (SwingUtilities.isEventDispatchThread()) {
            log.warn("getHttpRequest called on EDT — may block UI");
        }
        try {
            restTemplate.exchange(String.format(BASE_URL, "change-status?location=" + location),
                    HttpMethod.PUT,
                    getHttpRequest(),
                    Void.class);
            notifyObservers();
        } catch (HttpClientErrorException e) {
            throw new ApplicationException(e.getResponseBodyAsString().isBlank() ? "Unknown error" : e.getResponseBodyAsString());
        }
    }


    private class RefreshListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
                notifyObservers();
        }
    }
}
