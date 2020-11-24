package dntt.efim;

import dntt.entities.Item;
import dntt.entities.ProfitTable;

import java.util.HashMap;

/**
 * Contains the information of item and its profit
 */
@Deprecated()
public class ItemMeta {
    private HashMap<Item, Integer> itemProfitMap;

    public ItemMeta() {
        itemProfitMap = new HashMap<>();
    }

    public ItemMeta(ProfitTable profitTable) {
        itemProfitMap = new HashMap<>();
        this.itemProfitProcess(profitTable);
    }

    public void itemProfitProcess(ProfitTable profitTable) {
        profitTable.getItemProfitMap().forEach((item, profit) -> {
            itemProfitMap.put(item, profit);
        });
    }

    public HashMap<Item, Integer> getItemProfitMap() {
        return itemProfitMap;
    }

    public void setItemProfitMap(HashMap<Item, Integer> itemProfitMap) {
        this.itemProfitMap = itemProfitMap;
    }
}
