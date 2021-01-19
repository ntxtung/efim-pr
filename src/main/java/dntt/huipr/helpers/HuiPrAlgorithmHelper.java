package dntt.huipr.helpers;

import dntt.entities.*;

import java.util.LinkedHashSet;

public class HuiPrAlgorithmHelper {
    public static LinkedHashSet<Item> followingItemOf(Item item, Transaction transaction) {
        LinkedHashSet<Item> result = new LinkedHashSet<>();
        boolean isChecked = false;
        for (Item iteItem : transaction.getItemQuantityMap().keySet()) {
            if (isChecked) {
                result.add(iteItem);
            } else if (iteItem.equals(item)) {
                isChecked = true;
            }
        }
        return result;
    }
    public static Integer calculateStrictRemainingUtility(ItemSet itemSet, Transaction transaction, Item item, ProfitTable profitTable, LinkedHashSet<Item> followingItem) {
        LinkedHashSet<Item> remainingItem = new LinkedHashSet<>(followingItem);
        remainingItem.retainAll(transaction.getItemQuantityMap().keySet());
        for (Item fi : itemSet.getSet()) {
            var fiSet = followingItemOf(item, transaction);
            remainingItem.retainAll(fiSet);
        }
        var strictRemUtility = 0;
        for (Item i: remainingItem){
            strictRemUtility += transaction.getItemQuantityMap().get(i) * profitTable.getItemProfitMap().get(i);
        }
        return strictRemUtility;
    }

    public static Integer calculateStrictLocalUtility(Dataset dataset, ItemSet itemSet, Item item, ProfitTable profitTable, LinkedHashSet<Item> followingItem) {
        return 1;
    }

    public static Integer calculateItemUtilityInTransaction(Item item, Transaction transaction, ProfitTable profitTable) {
        return 1;
    }

    public static Integer calculateItemsetUtilityInTransaction(ItemSet itemset, Transaction transaction, ProfitTable profitTable) {
        var itemsetUtility = 0;
//        for (Item item : itemset.getSet()) {
//
//        }
        return 1;
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
            itemUtility += transaction.getItemQuantityMap().get(item) * profitTable.getItemProfitMap().get(item);
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
