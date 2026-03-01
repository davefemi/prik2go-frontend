package nl.davefemi.prik2go.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.davefemi.prik2go.controller.AuthController;
import nl.davefemi.prik2go.dto.OAuthRequestDTO;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class AuthClient {
    private static final RestTemplate restTemplate = new RestTemplate();
//    private static final String BASE_URL = "http://localhost:8080/%s";
//    private static final String BASE_URL = "https://prik2go.mangobeach-d8e4eeb8.germanywestcentral.azurecontainerapps.io/%s";
    private static final String BASE_URL = "https://prik2go.com/%s";
    private static final String LINK_OAUTH_USER = "private/oauth2/request/start?provider=%s";
    private static final String LOGIN_OAUTH_USER = "oauth2/request/start?provider=%s";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    private static synchronized HttpEntity<String> getHttpRequest() throws ApplicationException {
        SessionDTO session = AuthController.getSession();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(session.getToken());
        return new HttpEntity<>(AuthController.getSession().getUser().toString(), headers);
    }

    public static ResponseEntity<SessionDTO> loginUser(UserDTO user) throws ApplicationException {
        try {
            RequestEntity<UserDTO> httpRequest = new RequestEntity<>(
                    user,
                    HttpMethod.POST,
                    new URI(String.format(BASE_URL, "auth/login")),
                    UserDTO.class);
            return restTemplate.exchange(httpRequest, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            ExceptionHandler.handleHttpClientErrorException(e);
        } catch (RestClientException e) {
            throw new ApplicationException("Network is unreachable");
        } catch (Exception e) {
            throw new ApplicationException(e.getCause().getMessage());
        }
        return null;
    }

    public static void changePassword(UserDTO user) throws ApplicationException {
        try {
            RequestEntity<UserDTO> httpRequest = new RequestEntity<>(
                    user,
                    getHttpRequest().getHeaders(),
                    HttpMethod.PUT,
                    new URI(String.format(BASE_URL, "auth/change-password")),
                    UserDTO.class);
            restTemplate.exchange(httpRequest, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            ExceptionHandler.handleHttpClientErrorException(e);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public static boolean linkOAuth2User(String provider) throws ApplicationException {
        try {
            String path = String.format(LINK_OAUTH_USER, provider);
            RequestEntity<UserDTO> httpRequest = new RequestEntity<>(
                    getHttpRequest().getHeaders(),
                    HttpMethod.GET,
                    new URI(String.format(BASE_URL, path)));
            OAuthRequestDTO oAuthRequestDTO = oAuth2Login(httpRequest);
            if (isOAuthUserAuthenticated(oAuthRequestDTO)) {
                return true;
            } else {
                throw new ApplicationException("Oauth authentication failed");
            }
        } catch (URISyntaxException e) {
            throw new ApplicationException(e.getMessage());
        }
    }


    public static SessionDTO loginOAuth2User(String provider) throws ApplicationException {
        try {
            String path = String.format(LOGIN_OAUTH_USER, provider);
            RequestEntity<UserDTO> httpRequest = new RequestEntity<>(
                    HttpMethod.GET,
                    new URI(String.format(BASE_URL, path)));
            OAuthRequestDTO oAuthRequestDTO = oAuth2Login(httpRequest);
            if (isOAuthUserAuthenticated(oAuthRequestDTO)) {
                return getOauthSession(oAuthRequestDTO).getBody();
            } else {
                throw new ApplicationException("Oauth authentication failed");
            }
        } catch (URISyntaxException e) {
            throw new ApplicationException(e.getMessage());
        }
    }

        private static OAuthRequestDTO oAuth2Login(RequestEntity<UserDTO> httpRequest) throws ApplicationException {
        try {
            OAuthRequestDTO OAuthRequestDTO = restTemplate.exchange(httpRequest, OAuthRequestDTO.class).getBody();
            Desktop.getDesktop().browse(URI.create(OAuthRequestDTO.getUrl()));
            return OAuthRequestDTO;
        } catch (HttpClientErrorException e) {
            ExceptionHandler.handleHttpClientErrorException(e);
        } catch (RestClientException e) {
            throw new ApplicationException("Network is unreachable");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static boolean isOAuthUserAuthenticated(OAuthRequestDTO oAuthRequest) throws ApplicationException {
        try {
            int tries = 10;
            while (tries-- > -0) {
                RequestEntity<OAuthRequestDTO> httpRequest = new RequestEntity<>(
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
            ExceptionHandler.handleHttpClientErrorException(e);
        } catch (RestClientException e) {
            throw new ApplicationException("Network is unreachable");
        } catch (Exception e) {
            throw new ApplicationException(e.getCause().getMessage());
        }
        return false;
    }

    private static ResponseEntity<SessionDTO> getOauthSession(OAuthRequestDTO oAuthRequest) throws ApplicationException {
        try {
            RequestEntity<OAuthRequestDTO> httpRequest = new RequestEntity<>(
                    oAuthRequest,
                    HttpMethod.POST,
                    new URI(String.format(BASE_URL, "oauth2/request/get-session")),
                    OAuthRequestDTO.class);
            return restTemplate.exchange(httpRequest, SessionDTO.class);
        } catch (HttpClientErrorException e) {
            ExceptionHandler.handleHttpClientErrorException(e);
        } catch (RestClientException e) {
            throw new ApplicationException("Network is unreachable");
        } catch (Exception e) {
            throw new ApplicationException(e.getCause().getMessage());
        }
        return null;
    }

    public static boolean unlinkOAuth2User() throws ApplicationException {
        try {
            restTemplate.exchange(String.format(BASE_URL, "private/oauth2/revoke"),
                    HttpMethod.DELETE,
                    getHttpRequest(),
                    Void.class);
            return true;
        }
        catch (HttpClientErrorException e) {
            ExceptionHandler.handleHttpClientErrorException(e);
        }
        return false;
    }

}
