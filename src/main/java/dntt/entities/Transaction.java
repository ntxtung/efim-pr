package dntt.entities;

import java.util.ArrayList;
import java.util.HashMap;

public class Transaction {
    private HashMap<Item, Integer> itemUtilityMap;

    public Transaction() {
        itemUtilityMap = new HashMap<>();
    }

    public Transaction(HashMap<Item, Integer> itemQuantity) {
        this.itemUtilityMap = itemQuantity;
    }

    public HashMap<Item, Integer> getItemUtilityMap() {
        return itemUtilityMap;
    }

    public void setItemUtilityMap(HashMap<Item, Integer> itemUtilityMap) {
        this.itemUtilityMap = itemUtilityMap;
    }

    @Override
    public String toString() {
        ArrayList<String> s = new ArrayList<>();
        itemUtilityMap.forEach((item, quantity) -> s.add(String.format("%s: %s", item.getKey(), quantity)));
        return String.join(", ", s);
    }
}
