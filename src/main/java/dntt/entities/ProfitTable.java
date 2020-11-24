package dntt.entities;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfitTable {
    private HashMap<Item, Integer> itemProfitMap;

    public ProfitTable() {
        this.itemProfitMap = new HashMap<>();
    }

    public ProfitTable(HashMap<Item, Integer> itemProfitMap) {
        this.itemProfitMap = itemProfitMap;
    }

    public HashMap<Item, Integer> getItemProfitMap() {
        return itemProfitMap;
    }

    public void setItemProfitMap(HashMap<Item, Integer> itemProfitMap) {
        this.itemProfitMap = itemProfitMap;
    }

    @Override
    public String toString() {
        ArrayList<String> s = new ArrayList<>();
        itemProfitMap.forEach((item, profit) -> s.add(String.format("%s: %s", item, profit)));
        return String.join("\n", s);
    }
}
