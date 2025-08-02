package nl.davefemi.prik2go.client;

import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

public class AuthClient {
    private static final RestTemplate restTemplate = new RestTemplate();
    //    private static final String URL = "https://prik2go-backend.onrender.com/public/auth/%s";
    private static final String URL = "http://localhost:8080/public/auth/%s";

    public static ResponseEntity<String> loginUser(UserDTO user) throws ApplicatieException {
        try{
            RequestEntity<UserDTO> request =
                    new RequestEntity(
                            user,
                            HttpMethod.POST,
                            new URI(String.format(URL, "login")),
                            UserDTO.class);
            return restTemplate.exchange(request, String.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
    }
}
