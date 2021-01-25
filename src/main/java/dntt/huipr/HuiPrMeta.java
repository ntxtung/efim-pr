package dntt.huipr;

import dntt.huipr.exceptions.InvalidInputDataException;
import dntt.entities.Dataset;

public class HuiPrMeta {

    private DatasetMeta datasetMeta;

    public HuiPrMeta(Dataset dataset) throws InvalidInputDataException {
        this.datasetMeta = new DatasetMeta(dataset);
    }

    public HuiPrMeta(DatasetMeta datasetMeta) {
        this.datasetMeta = datasetMeta;
    }

    public DatasetMeta getDatasetMeta() {
        return datasetMeta;
    }

    public void setDatasetMeta(DatasetMeta datasetMeta) {
        this.datasetMeta = datasetMeta;
    }
}
