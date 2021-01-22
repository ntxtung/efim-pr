package dntt.huipr;

import dntt.entities.Dataset;
import dntt.entities.Item;
import dntt.entities.ItemSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

    private LinkedHashSet<TransactionMeta> transactionMetas;

    private HashMap<Item, Integer> localUtilityOfItemset;

    private HashMap<Item, Integer> subTreeUtilityOfItemset;

    public DatasetMeta(Dataset dataset) {
        this.dataset = dataset;
        transactionMetas = new LinkedHashSet<>();
//        utilityOfItemset = new HashMap<>();
        transactionWeightedUtility = new HashMap<>();
        localUtilityOfItemset = new HashMap<>();
        subTreeUtilityOfItemset = new HashMap<>();
    }

    public LinkedHashSet<TransactionMeta> getTransactionMetas() {
        return transactionMetas;
    }

    public void setTransactionMetas(LinkedHashSet<TransactionMeta> transactionMetas) {
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

    public HashMap<Item, Integer> getSubTreeUtilityOfItemset() {
        return subTreeUtilityOfItemset;
    }

    public void setSubTreeUtilityOfItemset(HashMap<Item, Integer> subTreeUtilityOfItemset) {
        this.subTreeUtilityOfItemset = subTreeUtilityOfItemset;
    }
}
