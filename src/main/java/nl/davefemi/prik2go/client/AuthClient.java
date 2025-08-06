package nl.davefemi.prik2go.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class AuthClient {
    private static final RestTemplate restTemplate = new RestTemplate();
//        private static final String URL = "https://prik2go-backend.onrender.com/public/auth/%s";
    private static final String URL = "http://localhost:8080/public/auth/%s";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public static ResponseEntity<SessionDTO> loginUser(UserDTO user) throws ApplicatieException {
        try{
            RequestEntity<UserDTO> request = new RequestEntity(
                            user,
                            HttpMethod.POST,
                            new URI(String.format(URL, "login")),
                            UserDTO.class);
            return restTemplate.exchange(request, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
    }
}
