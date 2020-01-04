package awakened.stafflog.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Staff {
    private ItemStack item;
    private int weight;

    @NotNull
    public Staff(@NotNull ItemStack item, int weight) {
        this.item = item;
        this.weight = weight;
    }

    @NotNull
    public ItemStack getItem() {
        return item;
    }

    public int getWeight() {
        return weight;
    }
}
