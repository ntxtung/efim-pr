package dntt.huipr;

import dntt.entities.Item;
import dntt.entities.ItemSet;
import dntt.entities.Transaction;

import java.util.HashMap;
import java.util.Objects;

/**
 * Contains the generated information of A transaction
 */
public class TransactionMeta {
    /**
     * Utility of an itemset in a transaction
     */
    private HashMap<ItemSet, Integer> utilityOfItemset;

    /**
     * Utility of transaction
     */
    private Integer utilityOfTransaction;

    private Transaction transaction;

    public TransactionMeta(Transaction transaction) {
        utilityOfItemset = new HashMap<>();
        this.transaction = transaction;
    }

    public HashMap<ItemSet, Integer> getUtilityOfItemset() {
        return utilityOfItemset;
    }

    public void setUtilityOfItemset(HashMap<ItemSet, Integer> utilityOfItemset) {
        this.utilityOfItemset = utilityOfItemset;
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

    @Override
    public String toString() {
        return "{" + transaction + ":" + this.utilityOfTransaction +'}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionMeta that = (TransactionMeta) o;
        return transaction.equals(that.transaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction);
    }
}
