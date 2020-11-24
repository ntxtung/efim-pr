package dntt.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ItemSet {
    private final Set<Item> set;

    public ItemSet() {
        this.set = new HashSet<>();
    }
    public ItemSet(Set<Item> set) {
        this.set = set;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ItemSet) {
            return this.set.equals(((ItemSet) o).set);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }
}
