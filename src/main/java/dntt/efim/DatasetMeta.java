package dntt.efim;

import dntt.entities.Dataset;
import dntt.entities.ItemSet;

import java.util.HashMap;

public class DatasetMeta {
    /**
     * Utility of an itemset in dataset
     */
    private HashMap<ItemSet, Integer> utilityOfItemset;
    /**
     * Transaction-weighted utility of an itemset in dataset
     */
    private HashMap<ItemSet, Integer> transactionWeightedUtility;
    /**
     * Total utility in dataset
     */
    private Integer utilityOfDataset;

    private Dataset dataset;

    private TransactionMeta transactionMeta;
    private ItemMeta itemMeta;

    public DatasetMeta(Dataset dataset) {
        this.dataset = dataset;
    }

    public HashMap<ItemSet, Integer> getUtilityOfItemset() {
        return utilityOfItemset;
    }

    public HashMap<ItemSet, Integer> getTransactionWeightedUtility() {
        return transactionWeightedUtility;
    }

    public Integer getUtilityOfDataset() {
        return utilityOfDataset;
    }

    public void setUtilityOfItemset(HashMap<ItemSet, Integer> utilityOfItemset) {
        this.utilityOfItemset = utilityOfItemset;
    }

    public void setTransactionWeightedUtility(HashMap<ItemSet, Integer> transactionWeightedUtility) {
        this.transactionWeightedUtility = transactionWeightedUtility;
    }

    public void setUtilityOfDataset(Integer utilityOfDataset) {
        this.utilityOfDataset = utilityOfDataset;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public TransactionMeta getTransactionMeta() {
        return transactionMeta;
    }

    public void setTransactionMeta(TransactionMeta transactionMeta) {
        this.transactionMeta = transactionMeta;
    }

    public ItemMeta getItemMeta() {
        return itemMeta;
    }

    public void setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }
}
