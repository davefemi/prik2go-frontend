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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.awt.desktop.QuitStrategy;
import java.io.IOError;
import java.io.IOException;
import java.net.URI;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class AuthClient {
    private static final RestTemplate restTemplate = new RestTemplate();
//        private static final String URL = "https://prik2go-backend.onrender.com/auth/%s";
    private static final String URL = "http://localhost:8080/%s";

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
        try{
            RequestEntity<UserDTO> request = new RequestEntity(
                    user,
                    HttpMethod.POST,
                    new URI(String.format(URL, "auth/login")),
                    UserDTO.class);
            return restTemplate.exchange(request, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (RestClientException e){
            throw new ApplicatieException("Network is unreachable");
        }
        catch (Exception e) {
            throw new ApplicatieException(e.getCause().getMessage());
        }
    }

    public static ResponseEntity<SessionDTO> changePassword(UserDTO user) throws ApplicatieException {
        try{
            RequestEntity<UserDTO> request = new RequestEntity(
                    user,
                    getHttpRequest().getHeaders(),
                    HttpMethod.POST,
                    new URI(String.format(URL, "auth/change-password")),
                    UserDTO.class);
            return restTemplate.exchange(request, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException(e.getResponseBodyAsString().isBlank()? "Authentication failed" : e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
    }

    public static void thirdPartyLogin() throws ApplicatieException {
        SessionDTO sessionDTO = Authenticator.getSession();
        try{
            RequestEntity<UserDTO> request = new RequestEntity(
                    getOauth2HttpRequest().getHeaders(),
                    HttpMethod.GET,
                    new URI(String.format(URL, "private/oauth2/request/start")));
            AuthResponseDTO dto = restTemplate.exchange(request, AuthResponseDTO.class).getBody();
            Desktop.getDesktop().browse(URI.create(dto.getUrl()));
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (RestClientException e){
            throw new ApplicatieException("Network is unreachable");
        }
        catch (Exception e) {
            throw new ApplicatieException(e.getCause().getMessage());
        }
    }
}
