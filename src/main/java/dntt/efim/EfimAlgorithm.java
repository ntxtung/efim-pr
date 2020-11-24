package dntt.efim;

public class EfimAlgorithm {
    private EfimMeta efimMeta;

    public EfimAlgorithm(EfimMeta efimMeta) {
        this.efimMeta = efimMeta;
    }

    /**
     * Calculate utility of item in transaction
     * Calculate utility of itemset in transaction
     * Calculate total utility of transaction
     * Calculate transaction-weight utility of item/itemset in dataset
     * Calculate utility of item/itemset in dataset
     * Calculate total utility of dataset
     */
    public void calculatePreMeta() {
        // TODO
    }

    public EfimMeta getEfimMeta() {
        return efimMeta;
    }

    public void setEfimMeta(EfimMeta efimMeta) {
        this.efimMeta = efimMeta;
    }
}
