package dntt.efim;

import dntt.efim.exceptions.InvalidInputDataException;
import dntt.entities.Dataset;
import dntt.entities.Item;
import dntt.entities.ProfitTable;
import dntt.entities.Transaction;

import java.util.Map;

public class EfimMeta {

    private DatasetMeta datasetMeta;
    private ProfitTable profitTable;

    public EfimMeta(Dataset dataset, ProfitTable profitTable) throws InvalidInputDataException {
        checkValidInputDatasetAndProfitTable(dataset, profitTable);

        this.datasetMeta = new DatasetMeta(dataset);
        this.profitTable = profitTable;
    }

    public EfimMeta(DatasetMeta datasetMeta, ProfitTable profitTable) {
        this.datasetMeta = datasetMeta;
        this.profitTable = profitTable;
    }

    private void checkValidInputDatasetAndProfitTable(Dataset dataset, ProfitTable profitTable) throws InvalidInputDataException {
        for (Transaction transaction : dataset.getTransactions()) {
            for (Map.Entry<Item, Integer> entry : transaction.getItemQuantityMap().entrySet()) {
                Item item = entry.getKey();
                if (!profitTable.getItemProfitMap().containsKey(item)) {
                    throw new InvalidInputDataException(item.getKey());
                }
            }
        }
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
