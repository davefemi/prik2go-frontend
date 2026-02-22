package nl.davefemi.prik2go.controller;

import nl.davefemi.prik2go.gui.factory.components.authentication.ChangeForm;
import nl.davefemi.prik2go.gui.factory.components.authentication.LoginForm;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import nl.davefemi.prik2go.gui.factory.components.util.ActiveWindow;
import nl.davefemi.prik2go.gui.factory.components.util.MessageDialog;
import nl.davefemi.prik2go.service.AuthService;

import javax.naming.LimitExceededException;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class AuthController {
    private static final AuthService authService = new AuthService();
    private static final Logger log = Logger.getLogger(AuthController.class.getName());
    private static volatile SessionDTO session;
    private static final int RETRIES = 5;
    private static final AtomicBoolean googleAuth = new AtomicBoolean(false);

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
            googleAuth.set(false);
            return handleLogin();
        } catch (CancellationException e) {
            if (!isSessionValid())
            throw new CancellationException(e.getMessage());
        }
        catch (Exception e){
            throw new IllegalAccessException("Login failed: " + e.getMessage());
        }
        return true;
    }

    private static boolean handleLogin() throws Exception {
        int attempts = RETRIES;
        LoginForm form = null;
        while (!googleAuth.get() && attempts --> 0) {
            try {
                form = new LoginForm();
                UserDTO credentials = getUserCredentials(form);
                if (credentials == null) throw new CancellationException("Login cancelled");
                session = authService.loginUser(credentials);
                log.info("Authentication successful");
                return true;
            } catch (Exception e) {
                if (e instanceof CancellationException){
                    throw new CancellationException(e.getMessage());
                }
                log.warning("Login failed: " + e.getMessage());
                MessageDialog.getErrorDialog(null, e.getMessage());
            }
        }
        if (googleAuth.get()){
            if(form != null)
                form.closeDialog();
            return true;
        }
        throw new LimitExceededException("Too many attempts");
    }

    private static UserDTO getUserCredentials(LoginForm form) throws Exception {
        form.addPropertyChangeListener("googleAuth", evt -> {
            if ((boolean) evt.getNewValue()) {
                log.info("Gelukt");
                googleAuth.set(true);
                form.closeDialog();
                ActiveWindow.bringToFront();
            }
            });
        return form.getUserLogin(session != null ? session.getUser() : null);
    }

    public static boolean changePassword() throws IllegalAccessException, LimitExceededException, ApplicationException {
        if (!isSessionValid()) {
            validateSession();
        }
        ChangeForm form = new ChangeForm();
            UserDTO dto = form.getUserInput();
            dto.setUser(session.getUser());
            authService.changePassword(dto);
            return true;
    }

//    public static boolean linkGoogleAccount() throws IllegalAccessException, ApplicationException {
//        if (!isSessionValid()){
//            validateSession();
//        }
//        try {
//            return authService.linkGoogleAccount();
//        } catch (ApplicationException e) {
//            throw new ApplicationException("Failed to Authenticate");
//        }
//    }

    public static boolean linkOAuth2User(String provider) throws ApplicationException{
        return authService.linkOAuth2User(provider);
    }

    public static boolean loginOAuth2User(String provider) throws ApplicationException {
        session = authService.loginOAuth2User(provider);
        if (isSessionValid()){
            return true;
        }
        return false;
    }
}
