package dntt.efim.exceptions;

public class InvalidInputDataException extends Exception {

    public InvalidInputDataException(String key) {
        super("Invalid Dataset and Profit table key mapping: "+ key);
    }

    public InvalidInputDataException() {
        super("Invalid Dataset and Profit table key mapping");
    }
}
