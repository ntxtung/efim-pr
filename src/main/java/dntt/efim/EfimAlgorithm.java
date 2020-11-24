package dntt.efim;

import dntt.entities.Item;
import dntt.entities.ProfitTable;
import dntt.entities.Transaction;

import java.util.HashMap;
import java.util.Map;

public class EfimAlgorithm {
    private EfimMeta efimMeta;

    public EfimAlgorithm(EfimMeta efimMeta) {
        this.efimMeta = efimMeta;
    }

    /**
     * Calculate utility of item in transaction
     * Calculate utility of itemset in transaction
     * Calculate total utility of transaction
     * Calculate transaction-weight utility of item/itemset in dataset
     * Calculate utility of item/itemset in dataset
     * Calculate total utility of dataset
     */
    public void calculatePreMeta() {
        for (Transaction transaction : efimMeta.getDatasetMeta().getDataset().getTransactions()) {
            this.calculatePreMetaTransaction(transaction);
        }
    }
    private void calculatePreMetaTransaction(Transaction transaction) {
        // Create new transaction meta
        TransactionMeta transactionMeta = new TransactionMeta(transaction);
        var itemsMetProfitMap = efimMeta.getProfitTable().getItemProfitMap();

        // Calculate utility of item in transaction for all <Item, Utility> map
        int transactionUtility = 0;
        for (Map.Entry<Item, Integer> itemQuantity : transactionMeta.getTransaction().getItemQuantityMap().entrySet()) {
            Item item = itemQuantity.getKey();

            // Utility of an item in transaction equals to multiply of its profit to its quantity in transaction
            int itemUtility = itemQuantity.getValue() * itemsMetProfitMap.get(item);
            transactionUtility += itemUtility;
            // Put the value to <Item, Utility> Map
            transactionMeta.getUtilityOfItem().put(item, itemUtility);
        }

        transactionMeta.setUtilityOfTransaction(transactionUtility);
        // Add transaction meta to dataset meta -> []transaction meta
        efimMeta.getDatasetMeta().getTransactionMetas().add(transactionMeta);
    }

    public void printSomething() {
        System.out.println("====================================");
        System.out.println("!!Dataset after calculate pre meta!!");
        System.out.println("!!!Only run after calculate pre meta");
        System.out.println("====================================");
        efimMeta.getDatasetMeta().getTransactionMetas().forEach(transactionMeta -> {
            transactionMeta.getUtilityOfItem().forEach((item, utility) -> {
                System.out.printf("%s:%d ", item, utility);
            });
            System.out.printf("| %d%n", transactionMeta.getUtilityOfTransaction());
        });
    }

    public EfimMeta getEfimMeta() {
        return efimMeta;
    }

    public void setEfimMeta(EfimMeta efimMeta) {
        this.efimMeta = efimMeta;
    }
}
