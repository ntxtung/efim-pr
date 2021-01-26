package dntt.huipr;

import dntt.entities.*;
import dntt.huipr.exceptions.InvalidInputDataException;

import java.util.*;
import java.util.stream.Collectors;

import static dntt.huipr.helpers.UpperBoundHelper.*;

public class HuiPrAlgorithm {
    private final HuiPrMeta efimMeta;
    private final Double minThreshold;
    private final LinkedHashSet<Item> followingItem = new LinkedHashSet<>();
    private final LinkedHashSet<Item> nextItem = new LinkedHashSet<>();
    private Boolean isDebugging = false;

    public HuiPrAlgorithm(HuiPrMeta efimMeta, Double minThreshold) {
        this.efimMeta = efimMeta;
        this.minThreshold = minThreshold;
    }

    public HuiPrAlgorithm(DatasetMeta datasetMeta, Double minThreshold) throws InvalidInputDataException {
        this.efimMeta = new HuiPrMeta(datasetMeta);
        this.minThreshold = minThreshold;
    }

    public void run() {
        if (isDebugging) {
            System.out.println("Pre-meta Transaction Calculating:");
            efimMeta.getDatasetMeta().getTransactionMetas().forEach(transMeta -> {
                transMeta.getTransaction().getItemUtilityMap().forEach((item, utility) -> System.out.printf("%s:%d ", item, utility));
                System.out.printf("| %d %n", transMeta.getUtilityOfTransaction());
            });
            System.out.println();
            System.out.println("Pre-meta Dataset Calculating:");
            System.out.println("Utility of dataset; " + this.efimMeta.getDatasetMeta().getUtilityOfDataset());
            System.out.println();
        }

        // HUI-PR algorithm
        calculateTransactionWeightUtility();
        calculateFollowingItems();
        removePromisingItemInDataset();
        sortEachTransactionAccordingToFollowingItem();
        calculateSubTreeUtility();
        calculatingNextItem();
        var result = recursiveSearch(null, efimMeta.getDatasetMeta().getDataset(), nextItem, followingItem, minThreshold, followingItem);
        System.out.println("Result: " + result);
    }

    private Transaction projectedTransactionOf(ItemSet itemSet, Transaction transaction) {
        Transaction projectedTransaction = new Transaction();
        LinkedHashMap<Item, Integer> projectedTransactionMap = new LinkedHashMap<>();
        Item lastContainItem = null;

        for (Item item : transaction.getItemUtilityMap().keySet()) {
            if (itemSet.getSet().contains(item)) {
                lastContainItem = item;
            }
        }

        if (lastContainItem == null) {
            return projectedTransaction;
        }

        for (Item item: itemSet.getSet()) {
            if (transaction.getItemUtilityMap().containsKey(item)) {
                projectedTransactionMap.put(item, transaction.getItemUtilityMap().get(item));
            }
        }

        boolean isMet = false;
        for (Item item: transaction.getItemUtilityMap().keySet()) {
            if (isMet == false) {
                if (item.equals(lastContainItem)) {
                    isMet = true;
                }
            } else {
                projectedTransactionMap.put(item, transaction.getItemUtilityMap().get(item));
            }
        }

        projectedTransaction.setItemUtilityMap(projectedTransactionMap);
        return projectedTransaction;
    }

    private Dataset projectedDatasetOf(ItemSet itemSet) {
        Dataset projectedDataset = new Dataset();
        for (Transaction transaction : efimMeta.getDatasetMeta().getDataset().getTransactions()) {
            projectedDataset.getTransactions().add(this.projectedTransactionOf(itemSet, transaction));
        }
        return projectedDataset;
    }

    private LinkedHashSet<ItemSet> recursiveSearch(
            ItemSet itemSetAlpha,
            Dataset projectedDatasetAlpha,
            LinkedHashSet<Item> nextItemAlpha,
            LinkedHashSet<Item> followingItemAlpha,
            Double minThreshold,
            LinkedHashSet<Item> twuOrderItem
    ) {
        LinkedHashSet<ItemSet> highUtilityItemset = new LinkedHashSet<>();
        System.out.println("/////////////////////////////////////////////////////////////////////////////////");
        System.out.println("On itemset");
        System.out.println(itemSetAlpha);

        System.out.println("On dataset");
        System.out.println(projectedDatasetAlpha);

        for (Item item: nextItemAlpha) {
            System.out.println("/////////////////////");
            ItemSet itemSetBeta = new ItemSet();
            if (itemSetAlpha != null) {
                itemSetBeta.getSet().addAll(itemSetAlpha.getSet());
            }
            itemSetBeta.getSet().add(item);
            var itemsetBetaUtility = calculateItemsetUtilityInDataset(itemSetBeta, projectedDatasetAlpha);
//            var calculatedMinThreshold = minThreshold * calculateDatasetUtility(projectedDatasetAlpha, efimMeta.getProfitTable());
            var calculatedMinThreshold = minThreshold * efimMeta.getDatasetMeta().getUtilityOfDataset();
            System.out.println("Itemset Beta");
            System.out.println(itemSetBeta);
            System.out.println("Itemset Beta Utility");
            System.out.println(itemsetBetaUtility);
            if (itemsetBetaUtility >= calculatedMinThreshold) {
                System.out.println("Satisfy itemset alpha utility, Added");
                highUtilityItemset.add(itemSetBeta);
                System.out.println("HUI now");
                System.out.println(highUtilityItemset);
                continue;
            }
            System.out.println("Unsatisfied itemset alpha utility, continue");
            Dataset projectedDatasetBeta = this.projectedDatasetOf(itemSetBeta);
            System.out.println("Projected dataset beta");
            System.out.println(projectedDatasetBeta);
            if (!projectedDatasetBeta.isEmpty()) {
                System.out.println("Projected dataset beta is not empty");
                LinkedHashMap<Item, Integer> strictLocalUtility = new LinkedHashMap<>();
                LinkedHashMap<Item, Integer> strictSubTreeUtility = new LinkedHashMap<>();
                for (Item iteFollowingItem : followingItemAlpha) {
                    strictLocalUtility.put(iteFollowingItem, calculateStrictLocalUtility(projectedDatasetBeta, itemSetBeta, iteFollowingItem, followingItemAlpha, twuOrderItem, projectedDatasetAlpha));
                    strictSubTreeUtility.put(iteFollowingItem, calculateStrictSubTreeUtility(projectedDatasetBeta, itemSetBeta, iteFollowingItem, followingItem));
                }
                LinkedHashSet<Item> followingItemBeta = new LinkedHashSet<>();
                for (Item iteLocItem : strictLocalUtility.keySet()) {
                    if (strictLocalUtility.get(iteLocItem) >= calculatedMinThreshold) {
                        followingItemBeta.add(iteLocItem);
                    }
                }
                LinkedHashSet<Item> nextItemBeta = new LinkedHashSet<>();
                for (Item iteLocItem : strictSubTreeUtility.keySet()) {
                    if (strictSubTreeUtility.get(iteLocItem) >= calculatedMinThreshold) {
                        nextItemBeta.add(iteLocItem);
                    }
                }
                System.out.println("Strict local utility");
                for (Item iteItem: strictLocalUtility.keySet()) {
                    System.out.printf("sloc(%s, %s): %d\n", itemSetBeta, iteItem, strictLocalUtility.get(iteItem));
                }
                System.out.println("Strict sub tree utility");
                for (Item iteItem: strictSubTreeUtility.keySet()) {
                    System.out.printf("ssub(%s, %s): %d\n", itemSetBeta, iteItem, strictSubTreeUtility.get(iteItem));
                }
                highUtilityItemset.addAll(recursiveSearch(itemSetBeta, projectedDatasetBeta, nextItemBeta, followingItemBeta, minThreshold, twuOrderItem));
            }
        }
        return highUtilityItemset;
    }

    private Integer subTreeUtility(ItemSet itemSet, Item item) {
        var subTreeUtility = 0;
        var itemSetUtility = 0;
        for (TransactionMeta transactionMeta : efimMeta.getDatasetMeta().getTransactionMetas()) {
            boolean isKeyFound = false;
            var subInTransaction = 0;
            for (Item keyItem : transactionMeta.getTransaction().getItemUtilityMap().keySet()) {
                if (!isKeyFound && item.equals(keyItem)) {
                    isKeyFound = true;
                }
                if (isKeyFound) {
                    subInTransaction += transactionMeta.getTransaction().getItemUtilityMap().get(keyItem);
                }
            }
            subTreeUtility += subInTransaction;
        }
        if (isDebugging) {
            System.out.printf("sub(%s, %s) = %d\n\n", itemSet, item, subTreeUtility);
        }
        return subTreeUtility;
    }

    private void calculateSubTreeUtility() {
        if (isDebugging) {
            System.out.println("Calculating sub tree utility of following item");
        }
        for (Item item : followingItem) {
            efimMeta.getDatasetMeta().getSubTreeUtilityOfItemset().put(item, subTreeUtility(null, item));
        }
    }

    private void sortEachTransactionAccordingToFollowingItem() {
        for (Transaction transaction : efimMeta.getDatasetMeta().getDataset().getTransactions()) {
            LinkedHashMap<Item, Integer> sortedTransaction = new LinkedHashMap<>();
            for (Item item : followingItem) {
                if (transaction.getItemUtilityMap().containsKey(item)) {
                    sortedTransaction.put(item, transaction.getItemUtilityMap().get(item));
                }
            }
            transaction.setItemUtilityMap(sortedTransaction);
        }
        if (isDebugging) {
            System.out.println("Sort each transaction according to following item");
            System.out.println(efimMeta.getDatasetMeta().getDataset());
        }
    }

    private void removePromisingItemInDataset() {
        for (Transaction transaction : efimMeta.getDatasetMeta().getDataset().getTransactions()) {
            HashMap<Item, Integer> promisingItem = new HashMap<>(transaction.getItemUtilityMap());
            for (Item item : transaction.getItemUtilityMap().keySet()) {
                if (!followingItem.contains(item)) {
                    promisingItem.remove(item);
                }
            }
            transaction.setItemUtilityMap(promisingItem);
        }
        if (this.isDebugging) {
            System.out.println("After remove unpromising item in dataset");
            System.out.println(efimMeta.getDatasetMeta().getDataset());
            System.out.println();
        }
    }

    private void calculatingNextItem() {
        var datasetUtility = efimMeta.getDatasetMeta().getUtilityOfDataset();
        var utilityThreshold = minThreshold * datasetUtility;
        for (Item item: followingItem) {
            if (efimMeta.getDatasetMeta().getSubTreeUtilityOfItemset().get(item) > utilityThreshold) {
                nextItem.add(item);
            }
        }
        if (this.isDebugging) {
            System.out.println("Next Item:");
            ArrayList<String> strResult = new ArrayList<>();
            nextItem.forEach(item -> strResult.add(item.toString()));
            System.out.println(String.join(" > ", strResult));
            System.out.println();
        }
    }

    private void calculateFollowingItems() {
        var datasetUtility = efimMeta.getDatasetMeta().getUtilityOfDataset();
        var utilityThreshold = minThreshold * datasetUtility;
        for (Item item: efimMeta.getDatasetMeta().getTransactionWeightedUtility().keySet()) {
            if (efimMeta.getDatasetMeta().getTransactionWeightedUtility().get(item) > utilityThreshold) {
                followingItem.add(item);
            }
        }
        if (this.isDebugging) {
            System.out.println("Following Item:");
            ArrayList<String> strResult = new ArrayList<>();
            followingItem.forEach(item -> strResult.add(item.toString()));
            System.out.println(String.join(" > ", strResult));
            System.out.println();
        }
    }

    private void calculatePreMetaTransaction(Transaction transaction) {
        // Create new transaction meta
        TransactionMeta transactionMeta = new TransactionMeta(transaction);

        // Calculate utility of item in transaction for all <Item, Utility> map
        int transactionUtility = 0;
        for (Map.Entry<Item, Integer> itemQuantity : transactionMeta.getTransaction().getItemUtilityMap().entrySet()) {
            Item item = itemQuantity.getKey();
            efimMeta.getDatasetMeta().getAllItem().add(item);
            // Utility of an item in transaction equals to multiply of its profit to its quantity in transaction
            int itemUtility = itemQuantity.getValue();
            transactionUtility += itemUtility;
        }

        // Set Utility of transaction
        transactionMeta.setUtilityOfTransaction(transactionUtility);

        // Add transaction meta to dataset meta -> []transaction meta
        efimMeta.getDatasetMeta().getTransactionMetas().add(transactionMeta);
    }

    private void calculatePreMetaDataset() {
        for (Transaction transaction : efimMeta.getDatasetMeta().getDataset().getTransactions()) {
            this.calculatePreMetaTransaction(transaction);
        }
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

    private void calculateTransactionWeightUtility() {
        Set<TransactionMeta> transactionMetas = efimMeta.getDatasetMeta().getTransactionMetas();
        // Calculate the TWU of all single Item Itemset
        // {A} {B} {C} ...
        for (Item item : efimMeta.getDatasetMeta().getAllItem()) {
            // Init value of itemset by 0
            var datasetTwu = efimMeta.getDatasetMeta().getTransactionWeightedUtility();
            datasetTwu.put(item, 0);

            for (TransactionMeta transactionMeta : transactionMetas) {
                // If transaction contain item
                if (transactionMeta.getTransaction().getItemUtilityMap().containsKey(item)) {
                    var oldValue = datasetTwu.get(item);
                    // Add up value by the transaction utility
                    datasetTwu.put(item, oldValue + transactionMeta.getUtilityOfTransaction());
                }
            }
            efimMeta.getDatasetMeta().getLocalUtilityOfItemset().putAll(datasetTwu);
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

        if (isDebugging) {
            System.out.println("TransactionWeightUtility:");
            for (Item item : efimMeta.getDatasetMeta().getTransactionWeightedUtility().keySet()) {
                System.out.printf("%s\n", item);
                System.out.printf("\t Itemset weight utility: %d", efimMeta.getDatasetMeta().getTransactionWeightedUtility().get(item));
                System.out.printf("\t Local utility: %d", efimMeta.getDatasetMeta().getLocalUtilityOfItemset().get(item));
                System.out.println();
            }
        }
    }
    public void setDebugging(Boolean debugging) {
        isDebugging = debugging;
    }
}
