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

    @Override
    public String toString() {
        ArrayList<String> s = new ArrayList<>();
        transactions.forEach((transaction) -> s.add(String.format("%s", transaction)));
        return String.join("\n", s);
    }
}
