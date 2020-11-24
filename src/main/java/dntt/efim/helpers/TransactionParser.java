package dntt.efim.helpers;

import dntt.entities.Item;
import dntt.entities.Transaction;

public class TransactionParser {
    // Sample string
    // "Coca:2,Pepsi:1,Cocoa:3"
    private final static String itemDelimiter = ",";
    private final static String quantityDelimiter = ":";

    public static Transaction from(String input) {
        Transaction transaction = new Transaction();
        String[] itemQuantities = input.split(itemDelimiter);
        for (String itemQuantity : itemQuantities) {
            String[] itemAndQuantity = itemQuantity.split(quantityDelimiter);
            transaction.getItemQuantityMap().put(new Item(itemAndQuantity[0]), Integer.parseInt(itemAndQuantity[1]));
        }
        return transaction;
    }
}
