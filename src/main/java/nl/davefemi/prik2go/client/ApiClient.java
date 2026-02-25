package nl.davefemi.prik2go.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.davefemi.prik2go.controller.AuthController;
import nl.davefemi.prik2go.dto.CustomerDTO;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import nl.davefemi.prik2go.observer.ApiSubject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ProblemDetail;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class ApiClient extends ApiSubject implements ApiClientInterface {
    private static final Log log = LogFactory.getLog(ApiClient.class);
    private final Timer timer = new Timer(1000, new RefreshListener());
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
//    private static final String BASE_URL = "https://prik2go-backend.onrender.com/private/locations/%s";
    private static final String BASE_URL = "http://localhost:8080/private/locations/%s";
//    private static final String BASE_URL = "https://prik2go.mangobeach-d8e4eeb8.germanywestcentral.azurecontainerapps.io/private/locations/%s";
//    private static final String BASE_URL = "https://prik2go.com/private/locations/%s";


    public ApiClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
        init();
    }

    private void init(){
//        startTimer();
    }

    private void startTimer(){
        timer.start();
    }

    private void stopTimer(){
        timer.stop();
    }

    private synchronized HttpEntity<String> getHttpRequest() throws IllegalAccessException {
        try {
            AuthController.validateSession();
            SessionDTO session = AuthController.getSession();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + session.getToken());
            return new HttpEntity<>(AuthController.getSession().getUser().toString(), headers);
        }
        catch (Exception e){
            throw new IllegalAccessException("No authorisation");
        }
    }

    @Override
    public ResponseEntity<List> getBranches() throws IllegalAccessException, ApplicationException {
        try {
            return restTemplate.exchange(String.format(BASE_URL, "get-branches"),
                    HttpMethod.POST,
                    getHttpRequest(),
                    List.class);
        } catch (HttpClientErrorException e) {
            if (MediaType.APPLICATION_PROBLEM_JSON.equals(e.getResponseHeaders().getContentType())){
                ProblemDetail pd = null;
                try {
                    pd = objectMapper.readValue(e.getResponseBodyAsString(), ProblemDetail.class);
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
                throw new ApplicationException(pd.getDetail());
            }
            throw new ApplicationException(e.getResponseBodyAsString());
        }
        catch (RestClientException e){
            throw new ApplicationException(e.getCause().getMessage());
        }
    }

    @Override
    public ResponseEntity<CustomerDTO> getCustomers(String location) throws IllegalAccessException, ApplicationException {
        try {
            return restTemplate.exchange(String.format(BASE_URL, "get-customers?location=" + location),
                    HttpMethod.POST,
                    getHttpRequest(),
                    CustomerDTO.class);
        } catch (HttpClientErrorException e) {
            if (MediaType.APPLICATION_PROBLEM_JSON.equals(e.getResponseHeaders().getContentType())) {
                ProblemDetail pd;
                try {
                    pd = objectMapper.readValue(e.getResponseBodyAsString(), ProblemDetail.class);
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
                throw new ApplicationException(pd.getDetail());
            }
            throw new ApplicationException(e.getResponseBodyAsString());
        }
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
            if (MediaType.APPLICATION_PROBLEM_JSON.equals(e.getResponseHeaders().getContentType())){
                ProblemDetail pd;
                try {
                    pd = objectMapper.readValue(e.getResponseBodyAsString(), ProblemDetail.class);
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
                throw new ApplicationException(pd.getDetail());
            }
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
            if (MediaType.APPLICATION_PROBLEM_JSON.equals(e.getResponseHeaders().getContentType())){
                ProblemDetail pd;
                try {
                    pd = objectMapper.readValue(e.getResponseBodyAsString(), ProblemDetail.class);
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
                throw new ApplicationException(pd.getDetail());
            }
            throw new ApplicationException(e.getResponseBodyAsString());
        }
    }


    private static class RefreshListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
//                notifyObservers();
        }
    }
}
