package dntt;

import dntt.entities.ItemSet;
import dntt.huipr.HuiPrAlgorithm;
import dntt.huipr.exceptions.InvalidInputDataException;
import dntt.huipr.helpers.ExportResultHelper;
import dntt.huipr.helpers.SpmfDatasetSerializer;

public class App {
    public static void main(String[] args) {
        String dataset = "test";
        Double minThresHold = 0.23;
        String file = "/"+dataset+".txt";
        try {
            HuiPrAlgorithm algorithm = new HuiPrAlgorithm(SpmfDatasetSerializer.loadFrom(file), minThresHold);
            algorithm.setDebugging(true);
            long startTime = System.currentTimeMillis();
            var result = algorithm.run();
            long endTime = System.currentTimeMillis();
            System.out.println("Run time: " + (endTime - startTime));
            System.out.println("Result: ");
            for (ItemSet itemSet: result) {
                System.out.println(itemSet);
            }
            ExportResultHelper.exportFrom(result, "res/"+dataset+".output");
        } catch (InvalidInputDataException e) {
            e.printStackTrace();
        }
    }
}
