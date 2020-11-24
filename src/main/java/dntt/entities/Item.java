package dntt.entities;

import java.util.Objects;

public class Item {
    private String key;

    public Item(String itemKey) {
        this.key = itemKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Item) {
            return this.key.equals(((Item) o).key);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
