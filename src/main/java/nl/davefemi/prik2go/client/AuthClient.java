package nl.davefemi.prik2go.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.davefemi.prik2go.authentication.Authenticator;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class AuthClient {
    private static final RestTemplate restTemplate = new RestTemplate();
//        private static final String URL = "https://prik2go-backend.onrender.com/public/auth/%s";
    private static final String URL = "http://localhost:8080/auth/%s";

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
        headers.set("Authorization", "Bearer " + session.getToken());
        return new HttpEntity<>(Authenticator.getSession().getUser().toString(), headers);
    }

    public static ResponseEntity<SessionDTO> loginUser(UserDTO user) throws ApplicatieException {
        try{
            RequestEntity<UserDTO> request = new RequestEntity(
                    user,
                    HttpMethod.POST,
                    new URI(String.format(URL, "public/login")),
                    UserDTO.class);
            return restTemplate.exchange(request, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
    }

    public static ResponseEntity<SessionDTO> changePassword(UserDTO user) throws ApplicatieException {
        try{
            RequestEntity<UserDTO> request = new RequestEntity(
                    user,
                    getHttpRequest().getHeaders(),
                    HttpMethod.POST,
                    new URI(String.format(URL, "private/change-password")),
                    UserDTO.class);
            return restTemplate.exchange(request, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException(e.getMessage());
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
    }
}
