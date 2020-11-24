package dntt.efim;

import dntt.entities.Dataset;
import dntt.entities.ProfitTable;

public class EfimMeta {

    private DatasetMeta datasetMeta;
    private ProfitTable profitTable;

    public EfimMeta(Dataset dataset, ProfitTable profitTable) {
        this.datasetMeta = new DatasetMeta(dataset);
        this.profitTable = profitTable;
    }

    public EfimMeta(DatasetMeta datasetMeta, ProfitTable profitTable) {
        this.datasetMeta = datasetMeta;
        this.profitTable = profitTable;
    }

    public ProfitTable getProfitTable() {
        return profitTable;
    }

    public DatasetMeta getDatasetMeta() {
        return datasetMeta;
    }

    public void setDatasetMeta(DatasetMeta datasetMeta) {
        this.datasetMeta = datasetMeta;
    }

    public void setProfitTable(ProfitTable profitTable) {
        this.profitTable = profitTable;
    }
}
