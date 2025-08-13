package nl.davefemi.prik2go.client;

import nl.davefemi.prik2go.authentication.Authenticator;
import nl.davefemi.prik2go.dto.KlantenDTO;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.observer.ApiSubject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class ApiClient extends ApiSubject implements ApiClientInterface {
    private static final Log log = LogFactory.getLog(ApiClient.class);
    private final Timer timer = new Timer(1000, new RefreshListener());
    private final RestTemplate restTemplate;
//    private static final String URL = "https://prik2go-backend.onrender.com/private/locations/%s";
    private static final String URL = "http://localhost:8080/private/locations/%s";

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

    private synchronized HttpEntity<String> getHttpRequest() throws IllegalAccessException, ApplicatieException {
        if (Authenticator.validateSession()) {
            SessionDTO session = Authenticator.getSession();
            if (session == null){
                throw new IllegalAccessException("No authorisation");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + session.getToken());
            return new HttpEntity<>(Authenticator.getSession().getUser().toString(), headers);
        }
        throw new IllegalAccessException("No authorisation");
    }

    @Override
    public ResponseEntity<List> getBranches() throws ApplicatieException, IllegalAccessException {
        return restTemplate.exchange(String.format(URL, "get-branches"),
                HttpMethod.POST,
                getHttpRequest(),
                List.class);
    }

    @Override
    public ResponseEntity<KlantenDTO> getCustomers(String location) throws IllegalAccessException, ApplicatieException {
        return restTemplate.exchange(String.format(URL, "get-customers?location=" + location),
                        HttpMethod.POST,
                        getHttpRequest(),
                        KlantenDTO.class);
    }

    @Override
    public ResponseEntity<Boolean> getBranchStatus(String location) throws ApplicatieException, IllegalAccessException {
        return restTemplate.exchange(String.format(URL, "get-status?location=" + location),
                        HttpMethod.POST,
                        getHttpRequest(),
                        Boolean.class);
    }

    @Override
    public void changeBranchStatus(String location) throws ApplicatieException, IllegalAccessException {
        if (SwingUtilities.isEventDispatchThread()) {
            log.warn("getHttpRequest called on EDT — may block UI");
        }
        restTemplate.exchange(String.format(URL, "change-status?location=" + location),
                HttpMethod.PUT,
                getHttpRequest(),
                Void.class);
        notifyObservers();
    }


    private class RefreshListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
//                notifyObservers();
        }
    }
}
