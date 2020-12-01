package dntt.huipr;

import dntt.huipr.exceptions.InvalidInputDataException;
import dntt.entities.Dataset;
import dntt.entities.Item;
import dntt.entities.ProfitTable;
import dntt.entities.Transaction;

import java.util.Map;

public class HuiPrMeta {

    private DatasetMeta datasetMeta;
    private ProfitTable profitTable;

    public HuiPrMeta(Dataset dataset, ProfitTable profitTable) throws InvalidInputDataException {
        checkValidInputDatasetAndProfitTable(dataset, profitTable);

        this.datasetMeta = new DatasetMeta(dataset);
        this.profitTable = profitTable;
    }

    public HuiPrMeta(DatasetMeta datasetMeta, ProfitTable profitTable) {
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
