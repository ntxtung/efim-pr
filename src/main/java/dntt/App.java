package dntt;

import dntt.entities.ItemSet;
import dntt.huipr.HuiPrAlgorithm;
import dntt.huipr.exceptions.InvalidInputDataException;
import dntt.huipr.helpers.SpmfDatasetSerializer;

public class App {
    public static void main(String[] args) {
        try {
            HuiPrAlgorithm algorithm = new HuiPrAlgorithm(SpmfDatasetSerializer.loadFrom("/test.txt"), 0.23);
//            HuiPrAlgorithm algorithm = new HuiPrAlgorithm(SpmfDatasetSerializer.loadFrom("/foodmart.txt"), 0.0015);
            algorithm.setDebugging(true);
            long startTime = System.currentTimeMillis();
            var result = algorithm.run();
            long endTime = System.currentTimeMillis();
            System.out.println("Run time: " + (endTime - startTime));
            System.out.println("Result: ");
            for (ItemSet itemSet: result) {
                System.out.println(itemSet);
            }
        } catch (InvalidInputDataException e) {
            e.printStackTrace();
        }
    }
}
