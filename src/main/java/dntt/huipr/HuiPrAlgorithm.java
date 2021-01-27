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

    public HuiPrAlgorithm(DatasetMeta datasetMeta, Double minThreshold) throws InvalidInputDataException {
        this.efimMeta = new HuiPrMeta(datasetMeta);
        this.minThreshold = minThreshold;
    }

    public HashSet<ItemSet> run() {
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
        return recursiveSearch(null, efimMeta.getDatasetMeta().getDataset(), nextItem, followingItem, minThreshold, followingItem);
    }

    private Transaction projectedTransactionOf(ItemSet itemSet, Transaction transaction) {
        Transaction projectedTransaction = new Transaction();

        Item lastItemInItemset = null;
        for (Item item : itemSet.getSet()) {
            lastItemInItemset = item;
            if (!transaction.getItemUtilityMap().containsKey(item)) {
                projectedTransaction.setItemUtilityMap(new LinkedHashMap<>());
                return projectedTransaction;
            }
        }

        LinkedHashMap<Item, Integer> projectedTransactionMap = new LinkedHashMap<>();
        boolean isMet = false;
        for (Item item : transaction.getItemUtilityMap().keySet()) {
            if (!isMet) {
                if (item.equals(lastItemInItemset)) {
                    isMet = true;
                }
                if (itemSet.getSet().contains(item)) {
                    projectedTransactionMap.put(item, transaction.getItemUtilityMap().get(item));
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

    private HashSet<ItemSet> recursiveSearch(
            ItemSet itemSetAlpha,
            Dataset projectedDatasetAlpha,
            LinkedHashSet<Item> nextItemAlpha,
            LinkedHashSet<Item> followingItemAlpha,
            Double minThreshold,
            LinkedHashSet<Item> twuOrderItem
    ) {
        HashSet<ItemSet> highUtilityItemset = new LinkedHashSet<>();

        for (Item item: nextItemAlpha) {
            ItemSet itemSetBeta = new ItemSet();
            if (itemSetAlpha != null) {
                itemSetBeta.getSet().addAll(itemSetAlpha.getSet());
            }
            itemSetBeta.getSet().add(item);
            var itemsetBetaUtility = calculateItemsetUtilityInDataset(itemSetBeta, projectedDatasetAlpha);
            var calculatedMinThreshold = minThreshold * efimMeta.getDatasetMeta().getUtilityOfDataset();
            if (itemsetBetaUtility >= calculatedMinThreshold) {
                highUtilityItemset.add(itemSetBeta);
                continue;
            }
            Dataset projectedDatasetBeta = this.projectedDatasetOf(itemSetBeta);
            if (!projectedDatasetBeta.isEmpty()) {
                LinkedHashMap<Item, Integer> strictLocalUtility = new LinkedHashMap<>();
                LinkedHashMap<Item, Integer> strictSubTreeUtility = new LinkedHashMap<>();
                for (Item iteFollowingItem : followingItemAlpha) {
                    strictLocalUtility.put(iteFollowingItem, calculateStrictLocalUtility(projectedDatasetBeta, itemSetBeta, iteFollowingItem, followingItemAlpha, twuOrderItem));
                    strictSubTreeUtility.put(iteFollowingItem, calculateStrictSubTreeUtility(projectedDatasetBeta, itemSetBeta, iteFollowingItem, followingItem));
                }
                LinkedHashSet<Item> followingItemBeta = new LinkedHashSet<>();
                for (Item iteLocItem : strictLocalUtility.keySet()) {
                    if (strictLocalUtility.get(iteLocItem) >= calculatedMinThreshold) {
                        followingItemBeta.add(iteLocItem);
                    }
                }
                LinkedHashSet<Item> nextItemBeta = new LinkedHashSet<>();
                for (Item iteSubItem : strictSubTreeUtility.keySet()) {
                    if (strictSubTreeUtility.get(iteSubItem) >= calculatedMinThreshold) {
                        nextItemBeta.add(iteSubItem);
                    }
                }
                highUtilityItemset.addAll(recursiveSearch(itemSetBeta, projectedDatasetBeta, nextItemBeta, followingItemBeta, minThreshold, twuOrderItem));
            }
        }
        return highUtilityItemset;
    }

    private Integer subTreeUtility(Item item) {
        var subTreeUtility = 0;
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
            System.out.printf("sub(%s, %s) = %d\n\n", null, item, subTreeUtility);
        }
        return subTreeUtility;
    }

    private void calculateSubTreeUtility() {
        if (isDebugging) {
            System.out.println("Calculating sub tree utility of following item");
        }
        for (Item item : followingItem) {
            efimMeta.getDatasetMeta().getSubTreeUtilityOfItemset().put(item, subTreeUtility(item));
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
