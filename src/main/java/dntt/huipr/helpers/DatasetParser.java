package dntt.huipr.helpers;

import dntt.entities.Dataset;
import dntt.entities.Transaction;

public class DatasetParser {
    // Sample data string
    // "Coca:2,Pepsi:1,Cocoa:3\n
    // "Coca:1,Snack:2,Milk:3\n
    // "Coconut:10,Coca:1,Milk:3"

    private final static String transactionDelimiter = "\n";

    public static Dataset from(String input) {
        Dataset dataset = new Dataset();
        String[] transactionStrings = input.split(transactionDelimiter);

        for (String transactionString: transactionStrings) {
            Transaction transaction = TransactionParser.from(transactionString);
            dataset.getTransactions().add(transaction);
        }

        return dataset;
    }
}
