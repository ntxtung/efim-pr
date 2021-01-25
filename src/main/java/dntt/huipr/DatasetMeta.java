package dntt.huipr;

import dntt.entities.Dataset;
import dntt.entities.Item;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Contain meta data of d dataset
 */
public class DatasetMeta {
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

    private HashSet<Item> allItem;

    public DatasetMeta(Dataset dataset) {
        this.dataset = dataset;
        transactionMetas = new LinkedHashSet<>();
        transactionWeightedUtility = new HashMap<>();
        localUtilityOfItemset = new HashMap<>();
        subTreeUtilityOfItemset = new HashMap<>();
        allItem = new HashSet<>();
    }

    public LinkedHashSet<TransactionMeta> getTransactionMetas() {
        return transactionMetas;
    }

    public void setTransactionMetas(LinkedHashSet<TransactionMeta> transactionMetas) {
        this.transactionMetas = transactionMetas;
    }

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

    public HashSet<Item> getAllItem() {
        return allItem;
    }

    public void setAllItem(HashSet<Item> allItem) {
        this.allItem = allItem;
    }
}
