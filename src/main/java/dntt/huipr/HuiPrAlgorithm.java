package dntt.huipr;

import dntt.entities.*;
import dntt.huipr.exceptions.InvalidInputDataException;

import java.util.*;
import java.util.stream.Collectors;

public class HuiPrAlgorithm {
    private final HuiPrMeta efimMeta;
    private final Double minThreshold;
    private final LinkedHashSet<Item> followingItem = new LinkedHashSet<>();

    public HuiPrAlgorithm(HuiPrMeta efimMeta, Double minThreshold) {
        this.efimMeta = efimMeta;
        this.minThreshold = minThreshold;
    }

    public HuiPrAlgorithm(Dataset dataset, ProfitTable profitTable, Double minThreshold) throws InvalidInputDataException {
        this.efimMeta = new HuiPrMeta(dataset, profitTable);
        this.minThreshold = minThreshold;
    }

    public void run() {
        // Precalculate something first
        for (Transaction transaction : efimMeta.getDatasetMeta().getDataset().getTransactions()) {
            this.calculatePreMetaTransaction(transaction);
        }
        this.calculatePreMetaDataset();

        // HUI-PR algorithm
        calculateLocalUtility();
        calculateTransactionWeightUtility();
        calculateFollowingItems();
    }

    private void calculateFollowingItems() {
        var datasetUtility = efimMeta.getDatasetMeta().getUtilityOfDataset();
        var utilityThreshold = minThreshold * datasetUtility;
        for (Item item: efimMeta.getDatasetMeta().getTransactionWeightedUtility().keySet()) {
            if (efimMeta.getDatasetMeta().getTransactionWeightedUtility().get(item) > utilityThreshold) {
                followingItem.add(item);
            }
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

        // Set Utility of transaction
        transactionMeta.setUtilityOfTransaction(transactionUtility);

        // Add transaction meta to dataset meta -> []transaction meta
        efimMeta.getDatasetMeta().getTransactionMetas().add(transactionMeta);
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
    }

    //!! Not yet
    private Integer remainingUtility(ItemSet itemSet, TransactionMeta transactionMeta) {
        var rem = transactionMeta.getUtilityOfTransaction();
        if (itemSet != null) {
            for (Item item: itemSet.getSet()) {
                rem -= transactionMeta.getUtilityOfItem().get(item);
            }
        }
        return rem;
    }

    //!! Not yet
    private Integer localUtility(ItemSet itemSet, Item item) {
        var itemSetUtility = 0;
        var rem = 0;

        for (TransactionMeta transactionMeta: efimMeta.getDatasetMeta().getTransactionMetas()) {
            if (transactionMeta.getTransaction().getItemQuantityMap().containsKey(item)) {
                rem += this.remainingUtility(itemSet, transactionMeta);
            }
        }

        return itemSetUtility + rem;
    }

    //!1 Not yet
    private void calculateLocalUtility() {
        for (Item item: efimMeta.getProfitTable().getItemProfitMap().keySet()) {
//            ItemSet itemset = new ItemSet();
//            itemset.getSet().add(item);
            efimMeta.getDatasetMeta().getLocalUtilityOfItemset().put(item, localUtility(null, item));
        }
    }

    private void calculateTransactionWeightUtility() {
        Set<TransactionMeta> transactionMetas = efimMeta.getDatasetMeta().getTransactionMetas();
        // Calculate the TWU of all single Item Itemset
        // {A} {B} {C} ...
        for (Item item : efimMeta.getProfitTable().getItemProfitMap().keySet()) {
            // Init value of itemset by 0
            var datasetTwu = efimMeta.getDatasetMeta().getTransactionWeightedUtility();
            datasetTwu.put(item, 0);

            for (TransactionMeta transactionMeta : transactionMetas) {
                // If transaction contain item
                if (transactionMeta.getTransaction().getItemQuantityMap().containsKey(item)) {
                    var oldValue = datasetTwu.get(item);
                    // Add up value by the transaction utility
                    datasetTwu.put(item, oldValue + transactionMeta.getUtilityOfTransaction());
                }
            }
        }
        // Sort the TWU
        var sortedMap =
                efimMeta.getDatasetMeta().getTransactionWeightedUtility().entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new
                                )
                        );
        efimMeta.getDatasetMeta().setTransactionWeightedUtility(sortedMap);
    }

    public void printReport() {
        System.out.println("====================================");
        efimMeta.getDatasetMeta().getTransactionMetas().forEach(transactionMeta -> {
            transactionMeta.getUtilityOfItem().forEach((item, utility) -> {
                System.out.printf("%s:%d ", item, utility);
            });
            System.out.printf("| %d %n", transactionMeta.getUtilityOfTransaction());
        });
        System.out.println("-------------------------------------");
        for (Item item : efimMeta.getDatasetMeta().getTransactionWeightedUtility().keySet()) {
            System.out.printf("%s\n", item);
//            System.out.printf("\t Itemset utility: %d", efimMeta.getDatasetMeta().getUtilityOfItemset().get(itemSet));
            System.out.printf("\t Itemset weight utility: %d", efimMeta.getDatasetMeta().getTransactionWeightedUtility().get(item));
            System.out.printf("\t Local utility: %d", efimMeta.getDatasetMeta().getLocalUtilityOfItemset().get(item));
            System.out.println();
        }
        System.out.println("-------------------------------------");
        ArrayList<String> followingItemStrList = new ArrayList<>();
        for (Item item: followingItem) {
            followingItemStrList.add(item.getKey());
        }
        String followingItemStr = String.join(" > ", followingItemStrList) + "\n";
        System.out.println(followingItemStr);
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
