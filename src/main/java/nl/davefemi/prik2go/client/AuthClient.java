package nl.davefemi.prik2go.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.davefemi.prik2go.authentication.Authenticator;
import nl.davefemi.prik2go.dto.AuthResponseDTO;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.swing.*;

public class AuthClient {
    private static final Timer timer = new Timer(100, new RefreshListener());
    private static final RestTemplate restTemplate = new RestTemplate();
    //        private static final String URL = "https://prik2go-backend.onrender.com/auth/%s";
    private static final String BASE_URL = "http://localhost:8080/%s";
    private static final String LINK_GOOGLE = "private/oauth2/request/start";
    private static final String LOGIN_GOOGLE = "oauth2/request/start";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    private static synchronized HttpEntity<String> getHttpRequest() throws IllegalAccessException, ApplicatieException {
        SessionDTO session = Authenticator.getSession();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(session.getToken());
        return new HttpEntity<>(Authenticator.getSession().getUser().toString(), headers);
    }

    private static synchronized HttpEntity<String> getOauth2HttpRequest() throws IllegalAccessException, ApplicatieException {
        SessionDTO session = Authenticator.getSession();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(session.getToken());
        return new HttpEntity<>(Authenticator.getSession().getUser().toString(), headers);
    }


    public static ResponseEntity<SessionDTO> loginUser(UserDTO user) throws ApplicatieException {
        try {
            RequestEntity<UserDTO> request = new RequestEntity(
                    user,
                    HttpMethod.POST,
                    new URI(String.format(BASE_URL, "auth/login")),
                    UserDTO.class);
            return restTemplate.exchange(request, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (RestClientException e) {
            throw new ApplicatieException("Network is unreachable");
        } catch (Exception e) {
            throw new ApplicatieException(e.getCause().getMessage());
        }
    }

    public static ResponseEntity<SessionDTO> changePassword(UserDTO user) throws ApplicatieException {
        try {
            RequestEntity<UserDTO> request = new RequestEntity(
                    user,
                    getHttpRequest().getHeaders(),
                    HttpMethod.POST,
                    new URI(String.format(BASE_URL, "auth/change-password")),
                    UserDTO.class);
            return restTemplate.exchange(request, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException(e.getResponseBodyAsString().isBlank() ? "Authentication failed" : e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
    }

    public static void linkGoogleAccount() throws ApplicatieException {
        try {
            RequestEntity<UserDTO> request = new RequestEntity(
                    getOauth2HttpRequest().getHeaders(),
                    HttpMethod.GET,
                    new URI(String.format(BASE_URL, LINK_GOOGLE)));
            thirdPartyLogin(request);
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
    }

    public static SessionDTO setLoginGoogle() throws ApplicatieException {
        try {
            RequestEntity<UserDTO> request = new RequestEntity(
                    HttpMethod.GET,
                    new URI(String.format(BASE_URL, LOGIN_GOOGLE)));
            AuthResponseDTO authResponseDTO = thirdPartyLogin(request);
            boolean res = userAuthenticated(authResponseDTO);
            if (res) {
                return getOauthSession(authResponseDTO).getBody();
            } else {
                throw new ApplicatieException("Oauth authentication failed");
            }
        } catch (URISyntaxException e) {
            throw new ApplicatieException(e.getMessage());
        }
    }

        public static AuthResponseDTO thirdPartyLogin(RequestEntity<UserDTO> request) throws ApplicatieException {
        try {
            AuthResponseDTO authResponseDTO = restTemplate.exchange(request, AuthResponseDTO.class).getBody();
            Desktop.getDesktop().browse(URI.create(authResponseDTO.getUrl()));
            return authResponseDTO;
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (RestClientException e) {
            throw new ApplicatieException("Network is unreachable");
        } catch (Exception e) {
            throw new ApplicatieException(e.getCause().getMessage());
        }
    }

    public static boolean userAuthenticated(AuthResponseDTO response) throws ApplicatieException {
        try {
            int tries = 10;
            while (tries-- > -0) {
                RequestEntity<AuthResponseDTO> request = new RequestEntity(
                        response,
                        HttpMethod.POST,
                        new URI(String.format(BASE_URL, "oauth2/request/polling")),
                        AuthResponseDTO.class);
                if (restTemplate.exchange(request, Boolean.class).getBody())
                    return true;
                Thread.sleep(2000);
            }
            return false;
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (RestClientException e) {
            throw new ApplicatieException("Network is unreachable");
        } catch (Exception e) {
            throw new ApplicatieException(e.getCause().getMessage());
        }
    }

    public static ResponseEntity<SessionDTO> getOauthSession(AuthResponseDTO authResponse) throws ApplicatieException {
        try {
            RequestEntity<AuthResponseDTO> request = new RequestEntity(
                    authResponse,
                    HttpMethod.POST,
                    new URI(String.format(BASE_URL, "oauth2/request/get-session")),
                    AuthResponseDTO.class);
            System.out.println("Gelukt");
            return restTemplate.exchange(request, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (RestClientException e) {
            throw new ApplicatieException("Network is unreachable");
        } catch (Exception e) {
            throw new ApplicatieException(e.getCause().getMessage());
        }
    }

    private static class RefreshListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
}
