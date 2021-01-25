package dntt;

import dntt.huipr.HuiPrAlgorithm;
import dntt.huipr.exceptions.InvalidInputDataException;
import dntt.entities.Dataset;
import dntt.huipr.helpers.DatasetParser;
import dntt.huipr.helpers.SpmfDatasetSerializer;

public class App {
    public static void main(String[] args) {
        try {
            HuiPrAlgorithm algorithm = new HuiPrAlgorithm(SpmfDatasetSerializer.loadFrom("/test.txt"), 0.23);
            algorithm.setDebugging(true);
            algorithm.run();
        } catch (InvalidInputDataException e) {
            e.printStackTrace();
        }
    }
}
