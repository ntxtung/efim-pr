package dntt.huipr;

import dntt.entities.Dataset;
import dntt.entities.Item;
import dntt.entities.ItemSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Contain meta data of d dataset
 */
public class DatasetMeta {
    /**
     * Utility of an itemset in dataset
     */
//    private HashMap<ItemSet, Integer> utilityOfItemset;
    /**
     * Transaction-weighted utility of an itemset in dataset
     */
    private HashMap<Item, Integer> transactionWeightedUtility;
    /**
     * Total utility in dataset
     */
    private Integer utilityOfDataset;

    private Dataset dataset;

    private HashSet<TransactionMeta> transactionMetas;

    private HashMap<Item, Integer> localUtilityOfItemset;

    public DatasetMeta(Dataset dataset) {
        this.dataset = dataset;
        transactionMetas = new HashSet<>();
//        utilityOfItemset = new HashMap<>();
        transactionWeightedUtility = new HashMap<>();
        localUtilityOfItemset = new HashMap<>();
    }

    public HashSet<TransactionMeta> getTransactionMetas() {
        return transactionMetas;
    }

    public void setTransactionMetas(HashSet<TransactionMeta> transactionMetas) {
        this.transactionMetas = transactionMetas;
    }

//    public HashMap<ItemSet, Integer> getUtilityOfItemset() {
//        return utilityOfItemset;
//    }

    public HashMap<Item, Integer> getTransactionWeightedUtility() {
        return transactionWeightedUtility;
    }

    public Integer getUtilityOfDataset() {
        return utilityOfDataset;
    }

    public void setTransactionWeightedUtility(HashMap<Item, Integer> transactionWeightedUtility) {
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

    public HashMap<Item, Integer> getLocalUtilityOfItemset() {
        return localUtilityOfItemset;
    }

    public void setLocalUtilityOfItemset(HashMap<Item, Integer> localUtilityOfItemset) {
        this.localUtilityOfItemset = localUtilityOfItemset;
    }
}