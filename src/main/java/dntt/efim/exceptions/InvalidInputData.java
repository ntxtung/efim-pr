package dntt.efim.exceptions;

public class InvalidInputData extends Exception {

    public InvalidInputData(String key) {
        super("Invalid Dataset and Profit table key mapping: "+ key);
    }

    public InvalidInputData() {
        super("Invalid Dataset and Profit table key mapping");
    }
}
