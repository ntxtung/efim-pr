package dntt.huipr;

import dntt.entities.*;
import dntt.huipr.exceptions.InvalidInputDataException;

import javax.xml.crypto.Data;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static dntt.huipr.helpers.HuiPrAlgorithmHelper.*;

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

    public HuiPrAlgorithm(Dataset dataset, ProfitTable profitTable, Double minThreshold) throws InvalidInputDataException {
        this.efimMeta = new HuiPrMeta(dataset, profitTable);
        this.minThreshold = minThreshold;
    }

    public void run() {
        if (this.isDebugging) {
            System.out.println("Input dataset: ");
            System.out.println(this.efimMeta.getDatasetMeta().getDataset());
            System.out.println();
            System.out.println("Input profit table: ");
            System.out.println(this.efimMeta.getProfitTable());
            System.out.println();
        }
        // Precalculate something first
        for (Transaction transaction : efimMeta.getDatasetMeta().getDataset().getTransactions()) {
            this.calculatePreMetaTransaction(transaction);
        }
        this.calculatePreMetaDataset();
        if (isDebugging) {
            System.out.println("Pre-meta Transaction Calculating:");
            efimMeta.getDatasetMeta().getTransactionMetas().forEach(transMeta -> {
                transMeta.getUtilityOfItem().forEach((item, utility) -> System.out.printf("%s:%d ", item, utility));
                System.out.printf("| %d %n", transMeta.getUtilityOfTransaction());
            });
            System.out.println();
            System.out.println("Pre-meta Dataset Calculating:");
            System.out.println("Utility of dataset; " + this.efimMeta.getDatasetMeta().getUtilityOfDataset());
            System.out.println();
        }

        // HUI-PR algorithm
        calculateLocalUtility();
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
        Item markLastItem = null;
        for (Item item: followingItem) {
            if (itemSet.getSet().contains(item)) {
                markLastItem = item;
            }
        }
        if (markLastItem == null) {
            return null;
        }
        boolean isMarked = false;
        for (Item item: transaction.getItemQuantityMap().keySet()) {
            if (isMarked) {
                if (transaction.getItemQuantityMap().keySet().containsAll(itemSet.getSet())) {
                    projectedTransactionMap.put(item, transaction.getItemQuantityMap().get(item));
                }
            } else {
                if (item.equals(markLastItem)) {
                    isMarked = true;
                }
            }
        }
        projectedTransaction.setItemQuantityMap(projectedTransactionMap);
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
            var itemsetBetaUtility = calculateItemsetUtilityInDataset(itemSetBeta, projectedDatasetAlpha, efimMeta.getProfitTable());
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
                    strictLocalUtility.put(iteFollowingItem, calculateStrictLocalUtility(projectedDatasetBeta, itemSetBeta, iteFollowingItem, efimMeta.getProfitTable(), followingItemAlpha, twuOrderItem));
                    strictSubTreeUtility.put(iteFollowingItem, calculateStrictSubTreeUtility(projectedDatasetBeta, itemSetBeta, iteFollowingItem, efimMeta.getProfitTable(), followingItem));
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
        highUtilityItemset = new LinkedHashSet<>();
        var a = new ItemSet();
        a.getSet().add(new Item("A"));
        a.getSet().add(new Item("B"));
        a.getSet().add(new Item("C"));
        a.getSet().add(new Item("D"));
        var b = new ItemSet();
        b.getSet().add(new Item("A"));
        b.getSet().add(new Item("C"));
        var c = new ItemSet();
        c.getSet().add(new Item("A"));
        c.getSet().add(new Item("D"));
        highUtilityItemset.add(a);
        highUtilityItemset.add(b);
        highUtilityItemset.add(c);
        return highUtilityItemset;
    }

    private Integer subTreeUtility(ItemSet itemSet, Item item) {
        var subTreeUtility = 0;
        var itemSetUtility = 0;
        for (TransactionMeta transactionMeta : efimMeta.getDatasetMeta().getTransactionMetas()) {
            boolean isKeyFound = false;
            var subInTransaction = 0;
            for (Item keyItem : transactionMeta.getTransaction().getItemQuantityMap().keySet()) {
                if (!isKeyFound && item.equals(keyItem)) {
                    isKeyFound = true;
                }
                if (isKeyFound) {
                    subInTransaction += transactionMeta.getUtilityOfItem().get(keyItem);
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
                if (transaction.getItemQuantityMap().containsKey(item)) {
                    sortedTransaction.put(item, transaction.getItemQuantityMap().get(item));
                }
            }
            transaction.setItemQuantityMap(sortedTransaction);
        }
        if (isDebugging) {
            System.out.println("Sort each transaction according to following item");
            System.out.println(efimMeta.getDatasetMeta().getDataset());
        }
    }

    private void removePromisingItemInDataset() {
        for (Transaction transaction : efimMeta.getDatasetMeta().getDataset().getTransactions()) {
            for (Item item : transaction.getItemQuantityMap().keySet()) {
                if (!followingItem.contains(item)) {
                    transaction.getItemQuantityMap().remove(item);
                }
            }
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

    //TODO: Wrong
    private Integer remainingUtility(ItemSet itemSet, TransactionMeta transactionMeta) {
        var rem = transactionMeta.getUtilityOfTransaction();
        if (itemSet != null) {
            for (Item item: itemSet.getSet()) {
                rem -= transactionMeta.getUtilityOfItem().get(item);
            }
        }
        return rem;
    }

    //TODO: Wrong
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

    //TODO: Not yet
    private void calculateLocalUtility() {
        for (Item item: efimMeta.getProfitTable().getItemProfitMap().keySet()) {
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
