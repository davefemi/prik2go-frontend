package nl.davefemi.prik2go.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.davefemi.prik2go.controller.AuthController;
import nl.davefemi.prik2go.dto.OAuthRequestDTO;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class AuthClient {
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://prik2go-backend.onrender.com/%s";
//    private static final String BASE_URL = "http://localhost:8080/%s";
    private static final String LINK_GOOGLE = "private/oauth2/request/start";
    private static final String LOGIN_GOOGLE = "oauth2/request/start";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    private static synchronized HttpEntity<String> getHttpRequest() throws IllegalAccessException, ApplicatieException {
        SessionDTO session = AuthController.getSession();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(session.getToken());
        return new HttpEntity<>(AuthController.getSession().getUser().toString(), headers);
    }

    private static synchronized HttpEntity<String> getOauth2HttpRequest() throws IllegalAccessException, ApplicatieException {
        SessionDTO session = AuthController.getSession();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(session.getToken());
        return new HttpEntity<>(AuthController.getSession().getUser().toString(), headers);
    }


    public static ResponseEntity<SessionDTO> loginUser(UserDTO user) throws ApplicatieException {
        try {
            RequestEntity<UserDTO> httpRequest = new RequestEntity(
                    user,
                    HttpMethod.POST,
                    new URI(String.format(BASE_URL, "auth/login")),
                    UserDTO.class);
            return restTemplate.exchange(httpRequest, SessionDTO.class);
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
            RequestEntity<UserDTO> httpRequest = new RequestEntity(
                    user,
                    getHttpRequest().getHeaders(),
                    HttpMethod.POST,
                    new URI(String.format(BASE_URL, "auth/change-password")),
                    UserDTO.class);
            return restTemplate.exchange(httpRequest, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException(e.getResponseBodyAsString().isBlank() ? "Authentication failed" : e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
    }

    public static boolean linkGoogleAccount() throws ApplicatieException {
        try {
            RequestEntity<UserDTO> httpRequest = new RequestEntity(
                    getOauth2HttpRequest().getHeaders(),
                    HttpMethod.GET,
                    new URI(String.format(BASE_URL, LINK_GOOGLE)));
            OAuthRequestDTO oAuthRequestDTO = oAuth2Login(httpRequest);
            if (isOAuthUserAuthenticated(oAuthRequestDTO)){
                return true;
            }
        } catch (Exception e) {
            throw new ApplicatieException(e.getMessage());
        }
        return false;
    }

    public static SessionDTO setLoginGoogle() throws ApplicatieException {
        try {
            RequestEntity<UserDTO> httpRequest = new RequestEntity(
                    HttpMethod.GET,
                    new URI(String.format(BASE_URL, LOGIN_GOOGLE)));
            OAuthRequestDTO oAuthRequestDTO = oAuth2Login(httpRequest);
            if (isOAuthUserAuthenticated(oAuthRequestDTO)) {
                return getOauthSession(oAuthRequestDTO).getBody();
            } else {
                throw new ApplicatieException("Oauth authentication failed");
            }
        } catch (URISyntaxException e) {
            throw new ApplicatieException(e.getMessage());
        }
    }

        private static OAuthRequestDTO oAuth2Login(RequestEntity<UserDTO> httpRequest) throws ApplicatieException {
        try {
            OAuthRequestDTO OAuthRequestDTO = restTemplate.exchange(httpRequest, OAuthRequestDTO.class).getBody();
            Desktop.getDesktop().browse(URI.create(OAuthRequestDTO.getUrl()));
            return OAuthRequestDTO;
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (RestClientException e) {
            throw new ApplicatieException("Network is unreachable");
        } catch (Exception e) {
            throw new ApplicatieException(e.getCause().getMessage());
        }
    }

    private static boolean isOAuthUserAuthenticated(OAuthRequestDTO oAuthRequest) throws ApplicatieException {
        try {
            int tries = 10;
            while (tries-- > -0) {
                RequestEntity<OAuthRequestDTO> httpRequest = new RequestEntity(
                        oAuthRequest,
                        HttpMethod.POST,
                        new URI(String.format(BASE_URL, "oauth2/request/polling")),
                        OAuthRequestDTO.class);
                if (restTemplate.exchange(httpRequest, Boolean.class).getBody())
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

    private static ResponseEntity<SessionDTO> getOauthSession(OAuthRequestDTO oAuthRequest) throws ApplicatieException {
        try {
            RequestEntity<OAuthRequestDTO> httpRequest = new RequestEntity(
                    oAuthRequest,
                    HttpMethod.POST,
                    new URI(String.format(BASE_URL, "oauth2/request/get-session")),
                    OAuthRequestDTO.class);
            return restTemplate.exchange(httpRequest, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            throw new ApplicatieException("Unauthorized: login failed");
        } catch (RestClientException e) {
            throw new ApplicatieException("Network is unreachable");
        } catch (Exception e) {
            throw new ApplicatieException(e.getCause().getMessage());
        }
    }
}
