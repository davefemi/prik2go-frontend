package nl.davefemi.prik2go.authentication;

import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.BerichtDialoog;
import nl.davefemi.prik2go.service.AuthService;

import javax.naming.LimitExceededException;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Authenticator {
    private static final AuthService authService = new AuthService();
    private static final Logger log = Logger.getLogger(Authenticator.class.getName());
    private static volatile SessionDTO session;
    private static final int RETRIES = 5;

    private static synchronized boolean isSessionValid() {
        return session != null && session.getExpiresAt().isAfter(Instant.now().plusSeconds(30));
    }

    public static synchronized SessionDTO getSession() {
        return session;
    }

    public static boolean validateSession() throws IllegalAccessException {
        if (isSessionValid()) {
            return true;
        }
        try {
            session = null;
            return handleLogin();
        } catch (CancellationException e) {
            throw new CancellationException(e.getMessage());
        }
        catch (Exception e){
            throw new IllegalAccessException("Login failed: " + e.getMessage());
        }
    }

    private static boolean handleLogin() throws Exception {
        int attempts = RETRIES;
        while (attempts-- > 0) {
            try {
                UserDTO credentials = getUserCredentials();
                if (credentials == null) throw new CancellationException("Login cancelled");
                session = authService.loginUser(credentials);
                log.info("Authentication successful");
                return true;
            } catch (Exception e) {
                if (e instanceof CancellationException){
                    throw new CancellationException(e.getMessage());
                }
                log.warning("Login failed: " + e.getMessage());
                BerichtDialoog.getErrorDialoog(null, e.getMessage());
            }
        }
        throw new LimitExceededException("Too many attempts");
    }

    private static UserDTO getUserCredentials() throws Exception {
        LoginForm form = new LoginForm();
        return form.getUserLogin(session != null ? session.getUser() : null);
    }

    public static boolean changePassword() throws IllegalAccessException, LimitExceededException, ApplicatieException {
        if (!isSessionValid()) {
            validateSession();
        }
        ChangeForm form = new ChangeForm();
            UserDTO dto = form.getUserInput();
            dto.setUser(session.getUser());
            authService.changePassword(dto);
            return true;
    }

    public static void linkGoogleAccount() throws IllegalAccessException {
        if (!isSessionValid()){
            validateSession();
        }
        try {
            authService.linkGoogleAccount();
        } catch (ApplicatieException e) {
            BerichtDialoog.getErrorDialoog(null, e.getMessage());
        }
    }

    public static boolean loginWithGoogle() throws ApplicatieException {
        if (authService.loginGoogleAccount()){
            session =
        };
    }
}
