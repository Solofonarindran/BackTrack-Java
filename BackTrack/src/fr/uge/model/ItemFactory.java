package fr.uge.model;

import java.util.List;
import java.util.ArrayList;

public class ItemFactory {

    private static List<ItemTreasure> catalog;
    
    public ItemFactory() {
    	catalog = initCatalog();
    }

    // Méthode qui initialise le catalogue des objets disponibles 
    private static List<ItemTreasure> initCatalog() {
    	List<ItemTreasure> list = new ArrayList<>();
        list.addAll(Weapon.weaponsAvailable());
        list.addAll(Armor.armorAvailable());
        list.addAll(Magic.magicElemAvailable());
        list.add(Gold.initGold());
        // a voir pour les types style <E>
        return List.copyOf(list);
    }
    
    // utile ?
    public static List<ItemTreasure> itemsAvailable() {
        return catalog;
    }
    
    // Méthode qui permet d'obtenir un objet en le recherchant avec son nom 
    public static ItemTreasure getItemByName(String name) {
        return catalog.stream()
                      .filter(i -> i.name().equals(name))
                      .findFirst()
                      .orElseThrow(() -> new IllegalArgumentException("Item inconnu : " + name));
    }
}
