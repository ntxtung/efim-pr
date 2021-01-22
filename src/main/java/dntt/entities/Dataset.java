package dntt.entities;

import java.util.ArrayList;
import java.util.List;

public class Dataset {
    private List<Transaction> transactions;

    public Dataset() {
        this.transactions = new ArrayList<>();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    public boolean isEmpty() {
        for (Transaction transaction: transactions) {
            if (!transaction.getItemQuantityMap().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    @Override
    public String toString() {
        ArrayList<String> s = new ArrayList<>();
        transactions.forEach((transaction) -> {
            if (!transaction.getItemQuantityMap().isEmpty()) {
                s.add(String.format("%s", transaction));
            }
        });
        return String.join("\n", s);
    }
}
