package fr.uge.model;

import java.util.List;
import java.util.ArrayList;

public class ItemFactory {

	private static final List<Item> catalog = initCatalog();
    
	private ItemFactory() {}

    // Méthode qui initialise le catalogue des objets disponibles 
    private static List<Item> initCatalog() {
    	List<Item> list = new ArrayList<>();
        list.addAll(Weapon.weaponsAvailable());
        list.addAll(Armor.armorAvailable());
        list.addAll(Magic.magicElemAvailable());
        list.add(Gold.initGold());
        list.add(ManaStone.initMana());
        return List.copyOf(list);
    }
    
    public static List<Item> getCatalog() {
        return catalog;
    }
    
    
    // Méthode qui permet d'obtenir un objet en le recherchant avec son nom 
    public static Item getItemByName(String name) {
        return catalog.stream()
                      .filter(i -> i.name().equals(name))
                      .findFirst()
                      .orElseThrow(() -> new IllegalArgumentException("Item inconnu : " + name));
    }
}
