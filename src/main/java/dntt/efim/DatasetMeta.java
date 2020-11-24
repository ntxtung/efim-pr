package dntt.efim;

import dntt.entities.Dataset;
import dntt.entities.ItemSet;
import dntt.entities.Transaction;

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

    private Set<TransactionMeta> transactionMetas;

    public DatasetMeta(Dataset dataset) {
        this.dataset = dataset;
        transactionMetas = new HashSet<>();
        utilityOfItemset = new HashMap<>();
        transactionWeightedUtility = new HashMap<>();
    }

    public Set<TransactionMeta> getTransactionMetas() {
        return transactionMetas;
    }

    public void setTransactionMetas(Set<TransactionMeta> transactionMetas) {
        this.transactionMetas = transactionMetas;
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

}
