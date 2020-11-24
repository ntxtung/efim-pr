package dntt;

import dntt.entities.Dataset;
import dntt.entities.ProfitTable;
import dntt.efim.helper.DatasetParser;
import dntt.efim.helper.ProfitTableParser;
import dntt.efim.EfimMeta;

public class App {
    public static void main(String[] args) {
        String datasetString =
                "A:3,B:3,D:1\n" +
                "A:3,B:7,C:1,E:3\n" +
                "A:2,C:1,F:3\n" +
                "C:2,D:1,E:3\n" +
                "A:2,B:1,D:3,E:1\n" +
                "A:2,B:1,D:3,E:1\n";
        String itemProfitString =
                "Coke:10\n" +
                "Pepsi:5\n" +
                "Cocoa:1\n" +
                "Snack:3\n" +
                "Milk:9\n" +
                "Meat:2";

        Dataset dataset = DatasetParser.from(datasetString);
        ProfitTable profitTable = ProfitTableParser.from(itemProfitString);

        EfimMeta efimMeta = new EfimMeta(dataset, profitTable);

        System.out.println("Input dataset: ");
        System.out.println(dataset);
        System.out.println("Input profit table: ");
        System.out.println(profitTable);
    }
}
