package dntt.entities;

import java.util.ArrayList;
import java.util.HashMap;

public class Transaction {
    private HashMap<Item, Integer> itemQuantityMap;

    public Transaction() {
        itemQuantityMap = new HashMap<>();
    }

    public Transaction(HashMap<Item, Integer> itemQuantity) {
        this.itemQuantityMap = itemQuantity;
    }

    public HashMap<Item, Integer> getItemQuantityMap() {
        return itemQuantityMap;
    }

    public void setItemQuantityMap(HashMap<Item, Integer> itemQuantityMap) {
        this.itemQuantityMap = itemQuantityMap;
    }

    @Override
    public String toString() {
        ArrayList<String> s = new ArrayList<>();
        itemQuantityMap.forEach((item, quantity) -> s.add(String.format("%s: %s", item.getKey(), quantity)));
        return String.join(", ", s);
    }
}
