package fr.uge.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fr.uge.model.Item;

public class LootZone {
    
    public enum LootType {
        COMBAT_REWARD,
        TREASURE_CHEST,
        MERCHANT_SALE
    }
    
    private final LootType type;
    private final List<Item> items;
    
    public LootZone(LootType type) {
        this.type = Objects.requireNonNull(type);
        this.items = new ArrayList<>();
    }
    
    public LootZone(LootType type, List<Item> items) {
        this.type = Objects.requireNonNull(type);
        this.items = new ArrayList<>(items);
    }
    
    public void addItem(Item item) {
        Objects.requireNonNull(item);
        items.add(item);
    }
    
    public void removeItem(Item item) {
        items.remove(item);
    }
    
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public LootType getType() {
        return type;
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int size() {
        return items.size();
    }
}