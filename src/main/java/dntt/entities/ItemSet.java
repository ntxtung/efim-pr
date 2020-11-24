package dntt.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ItemSet {
    private Set<Item> set;

    public ItemSet() {
        this.set = new HashSet<>();
    }
    public ItemSet(Set<Item> set) {
        this.set = set;
    }

    public Set<Item> getSet() {
        return set;
    }

    public void setSet(Set<Item> set) {
        this.set = set;
    }

    @Override
    public String toString() {
        ArrayList<String> strings = new ArrayList<>();
        for (Item item : set) {
            strings.add(item.getKey());
        }
        return String.format("{%s}", String.join(", ", strings));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ItemSet) {
            return this.set.equals(((ItemSet) o).set);
        } else {
            return false;
        }
    }
    // Todo
    @Override
    public int hashCode() {
        return Objects.hash(set);
    }
}
