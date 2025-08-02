package nl.davefemi.prik2go.client;

import nl.davefemi.prik2go.dto.KlantenDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import nl.davefemi.prik2go.observer.ApiSubject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ApiClient extends ApiSubject implements ApiClientInterface {
    private static final Log log = LogFactory.getLog(ApiClient.class);
    private final Timer timer = new Timer(1000, new RefreshListener());
    private final RestTemplate restTemplate;
//    private static final String URL = "https://prik2go-backend.onrender.com/private/locations/%s";
    private static final String URL = "http://localhost:8080/private/locations/%s";
    private String token;


    public ApiClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
        init();
    }

    private void init(){
        startTimer();
        log.info("Timer has started");
    }

    private void startTimer(){
        timer.start();
    }

    private void stopTimer(){
        timer.stop();
    }

    private HttpEntity<String> getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<String>(headers);
    }

    public void setToken(String token){
        this.token = token;
    }

    @Override
    public ResponseEntity<List> getBranches() throws ApplicatieException {
        ResponseEntity<List> response =
                restTemplate.exchange(String.format(URL, "get-branches"),
                HttpMethod.GET,
                getHeaders(),
                List.class);
        return response;
    }

    @Override
    public ResponseEntity<KlantenDTO> getCustomers(String location) {
        ResponseEntity<KlantenDTO> response =
                restTemplate.exchange(String.format(URL, "get-customers?location=" + location),
                        HttpMethod.GET,
                        getHeaders(),
                        KlantenDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<Boolean> getBranchStatus(String location) throws ApplicatieException {
        ResponseEntity<Boolean> response =
                restTemplate.exchange(String.format(URL, "get-status?location=" + location),
                        HttpMethod.GET,
                        getHeaders(),
                        Boolean.class);
        return response;
    }

    @Override
    public void changeBranchStatus(String location) throws VestigingException, ApplicatieException {
        restTemplate.exchange(String.format(URL, "change-status?location=" + location),
                HttpMethod.PUT,
                getHeaders(),
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
