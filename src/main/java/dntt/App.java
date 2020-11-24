package dntt;

import dntt.efim.exceptions.InvalidInputData;
import dntt.entities.Dataset;
import dntt.entities.ProfitTable;
import dntt.efim.helpers.DatasetParser;
import dntt.efim.helpers.ProfitTableParser;
import dntt.efim.EfimMeta;

public class App {
    public static void main(String[] args) {
        String datasetString =
                "A:3,B:3,D:1\n" +
                "A:3,B:7,C:1,E:3\n" +
                "A:4,C:3,F:1\n" +
                "C:1,D:4,E:10\n" +
                "A:4,B:4,D:2,E:6\n" +
                "A:6,B:2,D:2,E:1\n";
        String itemProfitString =
                "A:4\n" +
                "B:3\n" +
                "C:10\n" +
                "D:7\n" +
                "E:2\n" +
                "F:1";

        Dataset dataset = DatasetParser.from(datasetString);
        ProfitTable profitTable = ProfitTableParser.from(itemProfitString);

        System.out.println("Input dataset: ");
        System.out.println(dataset);
        System.out.println("Input profit table: ");
        System.out.println(profitTable);
        try {
            EfimMeta efimMeta = new EfimMeta(dataset, profitTable);
        } catch (InvalidInputData invalidInputData) {
            invalidInputData.printStackTrace();
        }

    }
}
