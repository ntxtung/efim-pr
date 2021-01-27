package dntt.huipr.helpers;

import dntt.entities.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class UpperBoundHelper {
    public static Boolean isPruned(ItemSet itemSet, Item item, LinkedHashSet<Item> followingItem, Transaction transaction) {
        if (itemSet.getSet().isEmpty()) {
            return false;
        }
        ArrayList<Item> followingItemList = new ArrayList<>(followingItem);
        followingItemList.retainAll(transaction.getItemUtilityMap().keySet());
        for (Item iteItem: itemSet.getSet()) {
            if (followingItemList.indexOf(item) - followingItemList.indexOf(iteItem) > 1) {
                return false;
            }
        }
        return true;
    }

    public static Integer calculateStrictSubTreeUtility(Dataset dataset, ItemSet itemSet, Item item, LinkedHashSet<Item> followingItem) {
        var strictSubTreeUtility = 0;
        if (itemSet.getSet().contains(item)) {
            return 0;
        }
        for (Transaction transaction : dataset.getTransactions()) {
            if (!transaction.getItemUtilityMap().containsKey(item)) {
                continue;
            }
            strictSubTreeUtility += calculateItemsetUtilityInTransaction(itemSet, transaction);
            strictSubTreeUtility += transaction.getItemUtilityMap().get(item);

            boolean isMet = false;
            var cloneFollowingItem = new LinkedHashSet<>(transaction.getItemUtilityMap().keySet());
            cloneFollowingItem.retainAll(followingItem);
            for (Item iteItem : cloneFollowingItem) {
                if (!isMet) {
                    if (iteItem.equals(item)) {
                        isMet = true;
                    }
                } else {
                    strictSubTreeUtility += transaction.getItemUtilityMap().get(iteItem);
                }
            }
        }
        return strictSubTreeUtility;
    }

    public static Integer calculateStrictLocalUtility(Dataset dataset, ItemSet itemSet, Item item, LinkedHashSet<Item> followingItem, LinkedHashSet<Item> twuOrderItem) {
        var strictLocalUtility = 0;
        for (Transaction transaction : dataset.getTransactions()) {
            if (!isPruned(itemSet, item, followingItem, transaction)) {
                if (transaction.getItemUtilityMap().containsKey(item)) {
                    strictLocalUtility += calculateItemsetUtilityInTransaction(itemSet, transaction);
                    strictLocalUtility += calculateStrictRemainingUtility(itemSet, transaction, twuOrderItem);
                }
            }
        }
        return strictLocalUtility;
    }

    public static Integer calculateStrictRemainingUtility(ItemSet itemSet, Transaction transaction, LinkedHashSet<Item> twuOrder) {
        LinkedHashSet<Item> remainingItem = new LinkedHashSet<>(twuOrder);
        remainingItem.retainAll(transaction.getItemUtilityMap().keySet());
        var itemsetList = new ArrayList<>(itemSet.getSet());
        var lastItemsetItem = itemsetList.get(itemsetList.size() - 1);

        var strictRemUtility = 0;
        var isMet = false;
        for (Item item: remainingItem) {
            if (!isMet) {
                if (item.equals(lastItemsetItem)) {
                    isMet = true;
                }
            } else {
                strictRemUtility += transaction.getItemUtilityMap().get(item);
            }
        }
        return strictRemUtility;
    }

    public static Integer calculateItemsetUtilityInTransaction(ItemSet itemset, Transaction transaction) {
        var itemsetUtility = 0;
        for (Item item : transaction.getItemUtilityMap().keySet()) {
            if (itemset.getSet().contains(item)) {
                itemsetUtility += transaction.getItemUtilityMap().get(item);
            }
        }
        return itemsetUtility;
    }

    public static Integer calculateItemsetUtilityInDataset(ItemSet itemSet, Dataset dataset) {
        var itemSetUtility = 0;
        for (Transaction transaction : dataset.getTransactions()) {
            boolean isValid = true;
            for (Item item : itemSet.getSet()) {
                if (!transaction.getItemUtilityMap().containsKey(item)) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                var itemsetUtilityInTransaction = 0;
                for (Item item : itemSet.getSet()) {
                    itemsetUtilityInTransaction += transaction.getItemUtilityMap().get(item);
                }
                itemSetUtility += itemsetUtilityInTransaction;
            }
        }
        return itemSetUtility;
    }
}
