package dntt.huipr.helpers;

import dntt.entities.Item;
import dntt.entities.ProfitTable;

public class ProfitTableParser {
    private final static String profitRowDelimiter = "\n";
    private final static String itemProfitDelimiter = ":";

    public static ProfitTable from(String input) {
        ProfitTable profitTable = new ProfitTable();
        String[] profitRowString = input.split(profitRowDelimiter);

        for (String profitRow: profitRowString) {
            String[] itemProfit = profitRow.split(itemProfitDelimiter);
            profitTable.getItemProfitMap().put(new Item(itemProfit[0]), Integer.parseInt(itemProfit[1]));
        }

        return profitTable;
    }
}
