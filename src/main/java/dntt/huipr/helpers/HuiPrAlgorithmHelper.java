package dntt.huipr.helpers;

import dntt.entities.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class HuiPrAlgorithmHelper {
    public static LinkedHashSet<Item> followingItemOf(Item item, Transaction transaction, LinkedHashSet<Item> followingItem) {
        LinkedHashSet<Item> result = new LinkedHashSet<>();
        boolean isChecked = false;
        for (Item iteItem : followingItem) {
            if (iteItem.equals(item)) {
                isChecked = true;
            }
            if (isChecked) {
                if (transaction.getItemQuantityMap().containsKey(iteItem)) {
                    result.add(iteItem);
                }
            }
        }
        return result;
    }

    public static LinkedHashSet<Item> followingItemOf(ItemSet itemSet, Transaction transaction, LinkedHashSet<Item> followingItem) {
        LinkedHashSet<Item> result = new LinkedHashSet<>(transaction.getItemQuantityMap().keySet());
        for (Item item : itemSet.getSet()) {
            result.retainAll(followingItemOf(item, transaction, followingItem));
        }
        return result;
    }

    public static Boolean isPruned(ItemSet itemSet, Item item, LinkedHashSet<Item> followingItem) {
        if (itemSet.getSet().isEmpty()) {
            return false;
        }
        ArrayList<Item> followingItemList = new ArrayList<>(followingItem);
        for (Item iteItem: itemSet.getSet()) {
            if (followingItemList.indexOf(item) - followingItemList.indexOf(iteItem) > 1) {
                return false;
            }
        }
        return true;
    }

    public static Integer calculateStrictSubTreeUtility(Dataset dataset, ItemSet itemSet, Item item, ProfitTable profitTable, LinkedHashSet<Item> followingItem) {
        var strictSubTreeUtility = 0;
        for (Transaction transaction : dataset.getTransactions()) {
            if (!transaction.getItemQuantityMap().containsKey(item)) {
                break;
            }
            for (Item checkItem : itemSet.getSet()) {
                if (!transaction.getItemQuantityMap().containsKey(checkItem)) {
                    break;
                }
            }
            strictSubTreeUtility += calculateItemsetUtilityInTransaction(itemSet, transaction, profitTable);
            strictSubTreeUtility += transaction.getItemQuantityMap().get(item) * profitTable.getItemProfitMap().get(item);
            var calculatedFollowingItem = followingItemOf(itemSet, transaction, followingItem);
            for (Item iteItem : calculatedFollowingItem) {
                if (followingItem.contains(iteItem)) {
                    strictSubTreeUtility += transaction.getItemQuantityMap().get(item) * profitTable.getItemProfitMap().get(iteItem);
                }
            }
        }
        return strictSubTreeUtility;
    }

    public static Integer calculateStrictLocalUtility(Dataset dataset, ItemSet itemSet, Item item, ProfitTable profitTable, LinkedHashSet<Item> followingItem, LinkedHashSet<Item> twuOrderItem) {
        var strictLocalUtility = 0;
        if (!isPruned(itemSet, item, followingItem)) {
            for (Transaction transaction : dataset.getTransactions()) {
                strictLocalUtility += calculateItemsetUtilityInTransaction(itemSet, transaction, profitTable);
                strictLocalUtility += calculateStrictRemainingUtility(itemSet, transaction, item, profitTable, twuOrderItem);
            }
        }
        return strictLocalUtility;
    }

    public static Integer calculateStrictRemainingUtility(ItemSet itemSet, Transaction transaction, Item item, ProfitTable profitTable, LinkedHashSet<Item> twuOrderItem) {
        LinkedHashSet<Item> remainingItem = new LinkedHashSet<>(twuOrderItem);
        remainingItem.retainAll(transaction.getItemQuantityMap().keySet());
        for (Item fi : itemSet.getSet()) {
            var fiSet = followingItemOf(fi, transaction, twuOrderItem);
            remainingItem.retainAll(fiSet);
        }
        var strictRemUtility = 0;
        for (Item i: remainingItem){
            strictRemUtility += transaction.getItemQuantityMap().get(i) * profitTable.getItemProfitMap().get(i);
        }
        return strictRemUtility;
    }

    public static Integer calculateItemsetUtilityInTransaction(ItemSet itemset, Transaction transaction, ProfitTable profitTable) {
        var itemsetUtility = 0;
        for (Item item : transaction.getItemQuantityMap().keySet()) {
            if (itemset.getSet().contains(item)) {
                itemsetUtility += transaction.getItemQuantityMap().get(item) * profitTable.getItemProfitMap().get(item);
            }
        }
        return itemsetUtility;
    }

    public static Integer calculateTransactionUtility(Transaction transaction, ProfitTable profitTable) {
        var transactionUtility = 0;
        var itemQuantityMap = transaction.getItemQuantityMap();
        for (Item item : itemQuantityMap.keySet()) {
            transactionUtility += itemQuantityMap.get(item) * profitTable.getItemProfitMap().get(item);
        }
        return transactionUtility;
    }

    public static Integer calculateDatasetUtility(Dataset dataset, ProfitTable profitTable) {
        var datasetUtility = 0;
        for (Transaction transaction: dataset.getTransactions()) {
            datasetUtility += calculateTransactionUtility(transaction, profitTable);
        }
        return datasetUtility;
    }

    public static Integer calculateItemUtilityInDataset(Item item, Dataset dataset, ProfitTable profitTable) {
        var itemUtility = 0;
        for (Transaction transaction: dataset.getTransactions()) {
            if (transaction.getItemQuantityMap().containsKey(item)) {
                itemUtility += transaction.getItemQuantityMap().get(item) * profitTable.getItemProfitMap().get(item);
            }
        }
        return itemUtility;
    }

    public static Integer calculateItemsetUtilityInDataset(ItemSet itemSet, Dataset dataset, ProfitTable profitTable) {
        var itemSetUtility = 0;
        for (Item item : itemSet.getSet()) {
            itemSetUtility += calculateItemUtilityInDataset(item, dataset, profitTable);
        }
        return itemSetUtility;
    }
}
