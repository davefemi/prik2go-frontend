package nl.davefemi.prik2go.authentication;

import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.BerichtDialoog;
import nl.davefemi.prik2go.service.AuthService;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class Authenticator {
    private static final AuthService authService = new AuthService();
    private static final Logger log = Logger.getLogger(Authenticator.class.getName());
    private static volatile SessionDTO session;
    private static final int RETRIES = 5;

    public static synchronized boolean isSessionValid() {
        return session != null && session.getExpiresAt().isAfter(Instant.now().plusSeconds(30));
    }

    public static synchronized SessionDTO getSession() {
        return session;
    }

    public static boolean ensureValidSession() throws IllegalAccessException {
        if (isSessionValid()) return true;

        try {
            return performLoginBlocking();
        } catch (Exception e) {
            throw new IllegalAccessException("Login failed: " + e.getMessage());
        }
    }

    private static boolean performLoginBlocking() throws Exception {
        int attempts = RETRIES;

        while (attempts-- > 0) {
            UserDTO credentials = promptCredentials();
            if (credentials == null) throw new CancellationException("Login cancelled");

            try {
                session = authService.loginUser(credentials);
                log.info("Authentication successful");
                return true;
            } catch (ApplicatieException e) {
                log.warning("Login failed: " + e.getMessage());
                BerichtDialoog.getErrorDialoog(null, e.getMessage());
            }
        }

        return false;
    }

    private static UserDTO promptCredentials() throws Exception {
        final UserDTO[] userHolder = new UserDTO[1];
        final Exception[] errorHolder = new Exception[1];
        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            try {
                LoginForm form = new LoginForm();
                userHolder[0] = form.getUserLogin(null);
            } catch (Exception e) {
                errorHolder[0] = e;
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        if (errorHolder[0] != null) throw errorHolder[0];
        return userHolder[0];
    }
}
