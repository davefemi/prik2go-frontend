package nl.davefemi.prik2go.exceptions;

@SuppressWarnings("serial")
public class ApplicationException extends Exception {

        public ApplicationException() {
                super();
        }

        public ApplicationException(String s) {
                super(s);
        }
}
