package dntt.efim;

import dntt.entities.Item;
import dntt.entities.ItemSet;
import dntt.entities.ProfitTable;
import dntt.entities.Transaction;

import java.util.*;

public class EfimAlgorithm {
    private final EfimMeta efimMeta;
    private List<ItemSet> allItemsetFromTransactions;

    public EfimAlgorithm(EfimMeta efimMeta) {
        this.efimMeta = efimMeta;
    }

    /**
     * Calculate utility of item in transaction                        // Done
     * Calculate utility of itemset in transaction                     // Done
     * Calculate total utility of transaction                          // Done
     *
     * Calculate transaction-weight utility of itemset in dataset      // Done
     * Calculate utility of itemset in dataset                         // Done
     * Calculate total utility of dataset                              // Done
     */
    public void calculatePreMeta() {
        for (Transaction transaction : efimMeta.getDatasetMeta().getDataset().getTransactions()) {
            this.calculatePreMetaTransaction(transaction);
        }
        this.calculatePreMetaDataset();
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


        // Set Utility of transaction
        transactionMeta.setUtilityOfTransaction(transactionUtility);


        // This list contains all item in transaction
        List<Item> allItemInTransaction = new LinkedList<>(transaction.getItemQuantityMap().keySet());

        // This list will contains all sets can generate from transaction
        List<List<Item>> allItemSet = new LinkedList<>();

        // Run combination algorithm
        for (int i = 1; i <= transaction.getItemQuantityMap().size(); i++) {
            allItemSet.addAll(combination(allItemInTransaction, i));
        }
        // Process generated set and calculate its utility
        for (List<Item> itemSetList : allItemSet) {
            ItemSet itemSet = new ItemSet();
            int itemSetUtility = 0;

            for (Item item: itemSetList) {
                itemSet.getSet().add(item);
                itemSetUtility += transactionMeta.getUtilityOfItem().get(item);
            }
            // Put to itemset utility map
            transactionMeta.getUtilityOfItemset().put(itemSet, itemSetUtility);
        }


        // Add transaction meta to dataset meta -> []transaction meta
        efimMeta.getDatasetMeta().getTransactionMetas().add(transactionMeta);


        // Preprocess for dataset meta process
        allItemsetFromTransactions = new ArrayList<>();
        for (List<Item> itemSetList : allItemSet) {
            ItemSet itemSet = new ItemSet();
            for (Item item: itemSetList) {
                itemSet.getSet().add(item);
            }
            allItemsetFromTransactions.add(itemSet);
        }
    }

    private void calculatePreMetaDataset() {
        Set<TransactionMeta> transactionMetas = efimMeta.getDatasetMeta().getTransactionMetas();

        // Calculate total utility of dataset
        // Utility of dataset equals to sum of all transaction utilities
        int totalUtility = 0;
        for (TransactionMeta transactionMeta : transactionMetas) {
            totalUtility += transactionMeta.getUtilityOfTransaction();
        }
        // Set the total utility of dataset
        efimMeta.getDatasetMeta().setUtilityOfDataset(totalUtility);

        // Calculate the utility of itemset in dataset
        // Calculate transaction-weight utility of itemset in dataset
        for (ItemSet itemSet : allItemsetFromTransactions) {
            int itemsetUtility = 0;
            int itemWeightUtility = 0;

            for (TransactionMeta transactionMeta : transactionMetas) {
                if (transactionMeta.getUtilityOfItemset().containsKey(itemSet)) {
                    itemsetUtility += transactionMeta.getUtilityOfItemset().get(itemSet);
                    itemWeightUtility += transactionMeta.getUtilityOfTransaction();
                }
            }
            efimMeta.getDatasetMeta().getUtilityOfItemset().put(itemSet, itemsetUtility);
            efimMeta.getDatasetMeta().getTransactionWeightedUtility().put(itemSet, itemWeightUtility);
        }


    }

    public void printReport() {
        System.out.println("====================================");
        System.out.println("!!Dataset after calculate pre meta!!");
        System.out.println("!!!Only run after calculate pre meta");
        System.out.println("====================================");
        efimMeta.getDatasetMeta().getTransactionMetas().forEach(transactionMeta -> {
            transactionMeta.getUtilityOfItem().forEach((item, utility) -> {
                System.out.printf("%s:%d ", item, utility);
            });
            System.out.printf("| %d %n", transactionMeta.getUtilityOfTransaction());
            System.out.print("\tItemset Utility -> ");
            transactionMeta.getUtilityOfItemset().forEach(((itemSet, utility) -> {
                System.out.printf(" %s:%d ", itemSet, utility);
            }));
            System.out.println();
        });
        System.out.println("-------------------------------------");
        for (ItemSet itemSet : allItemsetFromTransactions) {
            System.out.printf("%s\n",itemSet);
            System.out.printf("\t Itemset utility: %d", efimMeta.getDatasetMeta().getUtilityOfItemset().get(itemSet));
            System.out.printf("\t Itemset weight utility: %d", efimMeta.getDatasetMeta().getTransactionWeightedUtility().get(itemSet));
            System.out.println();
        }
        System.out.println("-------------------------------------");
        System.out.printf("Dataset Utility: %d", efimMeta.getDatasetMeta().getUtilityOfDataset());
    }

    // ♥ Thanks to: https://stackoverflow.com/questions/5162254/all-possible-combinations-of-an-array
    private static <T> List<List<T>> combination(List<T> values, int size) {

        if (0 == size) {
            return Collections.singletonList(Collections.emptyList());
        }

        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<T>> combination = new LinkedList<>();

        T actual = values.iterator().next();

        List<T> subSet = new LinkedList<>(values);
        subSet.remove(actual);

        List<List<T>> subSetCombination = combination(subSet, size - 1);

        for (List<T> set : subSetCombination) {
            List<T> newSet = new LinkedList<>(set);
            newSet.add(0, actual);
            combination.add(newSet);
        }
        combination.addAll(combination(subSet, size));

        return combination;
    }
}
