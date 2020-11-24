package dntt.efim;

import dntt.entities.Item;
import dntt.entities.ItemSet;
import dntt.entities.Transaction;

import java.util.HashMap;

/**
 *
 */
public class TransactionMeta {
    /**
     * Utility of an itemset in a transaction
     */
    private HashMap<ItemSet, Integer> utilityOfItemset;

    /**
     * Utility of an item in transaction
     */
    private HashMap<Item, Integer> utilityOfItem;

    /**
     * Utility of transaction
     */
    private Integer utilityOfTransaction;

    private Transaction transaction;

    public TransactionMeta(Transaction transaction) {
        utilityOfItemset = new HashMap<>();
        utilityOfItem = new HashMap<>();
        this.transaction = transaction;
    }

    public HashMap<ItemSet, Integer> getUtilityOfItemset() {
        return utilityOfItemset;
    }

    public void setUtilityOfItemset(HashMap<ItemSet, Integer> utilityOfItemset) {
        this.utilityOfItemset = utilityOfItemset;
    }

    public HashMap<Item, Integer> getUtilityOfItem() {
        return utilityOfItem;
    }

    public void setUtilityOfItem(HashMap<Item, Integer> utilityOfItem) {
        this.utilityOfItem = utilityOfItem;
    }

    public Integer getUtilityOfTransaction() {
        return utilityOfTransaction;
    }

    public void setUtilityOfTransaction(Integer utilityOfTransaction) {
        this.utilityOfTransaction = utilityOfTransaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
